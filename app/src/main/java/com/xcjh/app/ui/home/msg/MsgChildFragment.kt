package com.xcjh.app.ui.home.msg

import  android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.test.internal.util.LogUtil
import com.alibaba.fastjson.JSONObject
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.statelayout.StateConfig
import com.xcjh.app.MyApplication
import com.xcjh.app.R
import com.xcjh.app.adapter.MsgListAdapter
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.MsgListBean
import com.xcjh.app.databinding.FrMsgchildBinding
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.ui.feed.FeedNoticeActivity
import com.xcjh.app.ui.room.MsgBeanData
import com.xcjh.app.ui.room.MsgListNewData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.FeedSystemNoticeBean
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.listener.C2CListener
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MsgChildFragment : BaseFragment<MsgVm, FrMsgchildBinding>() {
    private val mAdapter by lazy { MsgListAdapter() }


    var chatId = "0"
    val empty by lazy { layoutInflater!!.inflate(R.layout.layout_empty, null) }

    companion object {

        fun newInstance(): MsgChildFragment {
            val args = Bundle()
            val fragment = MsgChildFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.rec.run {
            vertical()
            adapter = mAdapter
            distance(0, 0, 0, 30)
        }
        (mDatabind.rec.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false//防止item刷新的时候闪烁
        mAdapter.isEmptyViewEnable = true
        mDatabind.state.apply {
            StateConfig.setRetryIds(R.id.ivEmptyIcon, R.id.txtEmptyName)
            onEmpty {
                this.findViewById<TextView>(R.id.txtEmptyName).text =
                    resources.getString(R.string.nomsg)
                this.findViewById<ImageView>(R.id.ivEmptyIcon)
                    .setImageDrawable(resources.getDrawable(R.drawable.ic_empety_msg))
            }
        }
        mAdapter.addOnItemChildClickListener(R.id.lltDelete) { adapter, view, position ->
            var bean: MsgListNewData? = mAdapter.getItem(position)
            mViewModel.getDelMsg(bean?.anchorId.toString())
            deltDataToList(bean!!)
            mAdapter.removeAt(position)
            var listdata: MutableList<MsgListNewData> =
                mAdapter.items as MutableList<MsgListNewData>
            if (listdata.size == 0) {
                mDatabind.state.showEmpty()
            }
        }
        mAdapter.addOnItemChildClickListener(R.id.lltItem) { adapter, view, position ->
            var item: MsgListNewData = mAdapter.getItem(position)!!
            if (item.dataType == 2) {//反馈通知
                com.xcjh.base_lib.utils.startNewActivity<FeedNoticeActivity>()
            } else {
                com.xcjh.base_lib.utils.startNewActivity<ChatActivity>() {
                    if (item?.anchorId?.isNotEmpty() == true) {
                        this.putExtra(Constants.USER_ID, item?.anchorId)
                    } else {
                        this.putExtra(Constants.USER_ID, "")
                    }
                    if (item?.nick?.isNotEmpty() == true) {
                        this.putExtra(Constants.USER_NICK, item?.nick)
                    } else {
                        this.putExtra(Constants.USER_NICK, "")
                    }
                    if (item?.avatar?.isNotEmpty() == true) {
                        this.putExtra(Constants.USER_HEAD, item?.avatar)
                    } else {
                        this.putExtra(Constants.USER_HEAD, "")
                    }

                }
            }
            item.noReadSum = 0
            mViewModel.getDelMsg(item?.anchorId.toString())
            mAdapter[position] = item!!//更新Item数据
            addDataToList(item!!)


        }

        if (CacheUtil.isLogin()) {
            mViewModel.getMsgList(true, "")
            mDatabind.smartCommon.setOnRefreshListener {

                getRoomAllData()
                mViewModel.getMsgList(true, "")
            }.setOnLoadMoreListener {
                mViewModel.getMsgList(false, "")
            }
            initEvent()
        }
        //登录或者登出
        appViewModel.updateLoginEvent.observe(this) {
            if (it) {
                getRoomAllData()
                mViewModel.getMsgList(true, "")
                mDatabind.smartCommon.setOnRefreshListener {
                    getRoomAllData()
                    mViewModel.getMsgList(true, "")
                }.setOnLoadMoreListener {
                    mViewModel.getMsgList(false, "")
                }
                initEvent()
            } else {
                var listdata: MutableList<MsgListNewData> =
                    mAdapter.items as MutableList<MsgListNewData>
                listdata.clear()
                mAdapter.submitList(listdata)
                mAdapter.notifyDataSetChanged()
                GlobalScope.launch {
                    val data = getAll().await()

                    if (data.isNotEmpty()) {
                        for (i in data.indices) {
                            var bean =
                                MyApplication.dataChatList!!.chatDao?.findMessagesById(data[i].anchorId!!)
                            data[i].idd = bean!!.idd
                            //删除会显示在聊天列表的记录数据
                            MyApplication.dataChatList!!.chatDao?.delete(data[i])
                            //删除跟这个主播相关的连天记录
                            MyApplication.dataBase!!.chatDao?.deleteAllZeroId(data[i].anchorId!!)
                        }

                    }
                }
            }
        }

        getRoomAllData()

    }

    fun getRoomAllData() {
        GlobalScope.launch {
            val data = getAll().await()

            if (data.isNotEmpty()) {

                mAdapter.submitList(data)

            } else {
                mAdapter.emptyView = empty
            }
            mDatabind.smartCommon.finishRefresh()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initEvent() {
        MyWsManager.getInstance(requireActivity())!!
            .setC2CListener(javaClass.name, object : C2CListener {
                override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {
                    if (isOk) {

                    }
                }

                override fun onSystemMsgReceive(it: FeedSystemNoticeBean) {
                    //系统反馈通知推送消息
                    var chat = ReceiveChatMsg()
                    chat.id = it.noticeId.toString()
                    chat.msgType = 1
                    chat.content = it.reason!!
                    chat.createTime = it.createTime
                    chat.sent = 1
                    chat.dataType = 2
                    chat.toAvatar = ""
                    chat.from = "-1"
                    chat.anchorId = "-1"
                    chat.noReadSum = 1
                    chat.toNickName = ""
                    chat.fromAvatar = ""
                    chat.fromNickName = ""
                    refshMsg(chat)

                }

                override fun onC2CReceive(chat: ReceiveChatMsg) {
                    refshMsg(chat)
                }

                override fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>) {

                }
            })
        appViewModel.updateMsgEvent.observeForever {
            if (isAdded) {
                chatId = it
                if (it == "-1") {//清除消息红点
                    mViewModel.getClreaAllMsg()

                } else {

                }
            }
        }
        appViewModel.updateMsgListEvent.observeForever {
            if (isAdded) {

                var chat = ReceiveChatMsg()
                chat.id = it.id
                chat.chatType = it.chatType
                chat.msgType = it.msgType
                chat.cmd = it.cmd
                chat.anchorId = it.anchorId
                chat.content = it.content
                chat.createTime = it.createTime
                chat.from = it.fromId
                chat.sent = it.sent
                chat.groupId = it.groupId
                chat.toAvatar = it.avatar
                chat.toNickName = it.nick
                chat.fromAvatar = it.avatar
                chat.fromNickName = it.nick
                chat.level = it.level
                refshMsg(chat)
            }
        }
    }


    /***
     * 返回后一个时间戳是否比前一个时间戳早的布尔值
     */
    private fun isTimestampEarlier(timestamp1: Long, timestamp2: Long): Boolean {
        return timestamp2 < timestamp1
    }

    fun updataMsg(it: MsgListNewData) {
        var chat = ReceiveChatMsg()
        chat.id = it.id
        chat.msgType = it.msgType

        chat.content = it.content!!
        chat.createTime = it.createTime

        chat.sent = it.sent
        chat.dataType = it.dataType
        if (it.avatar == null) {
            chat.toAvatar = ""
        } else {
            chat.toAvatar = it.avatar
        }
        if (it.dataType == 2) {
            chat.from = "-1"
            chat.anchorId = "-1"
        } else {
            chat.from = it.fromId
            chat.anchorId = it.anchorId
        }
        chat.noReadSum = it.noReadSum
        chat.toNickName = it.nick
        chat.fromAvatar = it.avatar
        chat.fromNickName = it.nick
        refshMsg(chat)

    }

    override fun createObserver() {

        mViewModel.msgList.observe(this) {
            if (it.isSuccess) {
                var listdata: MutableList<MsgListNewData> =
                    mAdapter.items as MutableList<MsgListNewData>
                if (listdata.size > 0) {
                    for (i in 0 until it.listData.size) {
                        for (j in 0 until listdata.size) {
                            if (listdata[j].anchorId == it.listData[i].anchorId) {
                                if (isTimestampEarlier(
                                        it.listData[i].createTime,
                                        listdata[j].createTime
                                    )
                                ) {
                                    updataMsg(it.listData[i])
                                    break
                                }
                            }
                        }
                    }
                } else {
                    updataMsg(it.listData[0])
                }


            }
        }
        mViewModel.clreaAllMsg.observe(this) {
            appViewModel.updateMainMsgNum.postValue("0")
            mViewModel.getMsgList(true, "")
        }
    }

    override fun onDestroy() {
        MyWsManager.getInstance(requireActivity())!!.removeC2CListener(javaClass.name)
        super.onDestroy()
    }

    fun refshMsg(msg: ReceiveChatMsg) {

        try {
            var listdata: MutableList<MsgListNewData> =
                mAdapter.items as MutableList<MsgListNewData>
            var hasMsg = false
            for (i in 0 until listdata.size) {
                if (msg.anchorId == listdata[i].anchorId) {
                    hasMsg = true
                    var bean = MsgListNewData()
                    if (msg.anchorId == msg.from) {//主播发送的消息
                        bean.avatar = msg.fromAvatar ?: ""
                        if (chatId == msg.anchorId) {
                            bean.noReadSum = 0
                        } else {
                            bean.noReadSum = listdata[i].noReadSum + 1
                        }
                    } else {
                        bean.avatar = msg.toAvatar!!
                        bean.noReadSum = 0
                    }
                    bean.content = msg.content.toString()
                    bean.createTime = msg.createTime!!
                    bean.msgType = msg.msgType!!
                    bean.dataType = msg.dataType!!
                    bean.fromId = msg.from.toString()
                    bean.anchorId = msg.anchorId.toString()
                    bean.sent = msg.sent
                    bean.id = listdata[i].id

                    if (msg.anchorId == msg.from) {//主播发送的消息
                        bean.nick = msg.fromNickName!!
                    } else {
                        bean.nick = msg.toNickName!!
                    }

                    LogUtils.d("更新了哈哈$i")

                    mAdapter[i] = bean//更新Item数据
                    if (i != 0) {
                        GlobalScope.launch() {
                            delay(1000) // 延迟1秒
                            mAdapter.swap(i, 0)
                        }
                    }

                    //
                    addDataToList(bean)
                    break
                }
            }
            if (!hasMsg) {
                var bean = MsgListNewData()
                if (msg.anchorId == msg.from) {//主播发送的消息
                    bean.avatar = msg.fromAvatar ?: ""
                    if (chatId == msg.anchorId) {
                        bean.noReadSum = 0
                    } else {
                        bean.noReadSum = msg.noReadSum
                    }
                } else {
                    bean.avatar = msg.toAvatar!!
                    bean.noReadSum = 0
                }
                bean.content = msg.content.toString()
                bean.createTime = msg.createTime!!
                bean.msgType = msg.msgType!!
                bean.dataType = msg.dataType!!
                bean.fromId = msg.from ?: ""
                bean.anchorId = msg.anchorId ?: ""
                bean.id = msg.id
                bean.sent = msg.sent
                if (msg.anchorId == msg.from) {//主播发送的消息
                    bean.nick = msg.fromNickName ?: ""
                } else {
                    bean.nick = msg.toNickName ?: ""
                }
                LogUtils.d("鞥加了哈哈")

                addDataToList(bean)
                mAdapter.add(0, bean)

            }
        } catch (e: Exception) {
            LogUtils.d("出错了" + e.printStackTrace())
        }


    }

    fun addData(chat: ReceiveChatMsg) {
        if (chat.from == chat.anchorId) {
            var beanmy: MsgBeanData = MsgBeanData()
            beanmy.anchorId = chat.anchorId
            beanmy.fromId = chat.from
            beanmy.content = chat.content
            beanmy.chatType = chat.chatType
            beanmy.cmd = 11
            beanmy.msgType = chat.msgType
            beanmy.createTime = chat.createTime

            GlobalScope.launch {

                MyApplication.dataBase!!.chatDao?.insert(beanmy)

            }
        }
    }

    /***
     * 索取所有消息缓存
     */
    fun getAll(): Deferred<List<MsgListNewData>> {
        return GlobalScope.async {
            MyApplication.dataChatList!!.chatDao!!.getAll()
        }
    }

    /***
     * 添加或者更新新的数据
     */
    fun addDataToList(data: MsgListNewData) {
        GlobalScope.launch {
            MyApplication.dataChatList!!.chatDao?.insertOrUpdate(data)


        }
    }

    /***
     * 删除数据
     */
    fun deltDataToList(data: MsgListNewData) {
        GlobalScope.launch {
            var bean = MyApplication.dataChatList!!.chatDao?.findMessagesById(data.anchorId!!)

            data.idd = bean!!.idd
            //删除会显示在聊天列表的记录数据
            MyApplication.dataChatList!!.chatDao?.delete(data)

            //删除跟这个主播相关的连天记录
            MyApplication.dataBase!!.chatDao?.deleteAllZeroId(data.anchorId!!)

        }
    }

}