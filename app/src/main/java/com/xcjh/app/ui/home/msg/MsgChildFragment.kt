package com.xcjh.app.ui.home.msg

import  android.os.Bundle
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.test.internal.util.LogUtil
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.xcjh.app.MyApplication
import com.xcjh.app.R
import com.xcjh.app.adapter.MsgListAdapter
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.MsgListBean
import com.xcjh.app.databinding.FrMsgchildBinding
import com.xcjh.app.ui.room.MsgBeanData
import com.xcjh.app.ui.room.MsgListNewData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.listener.C2CListener
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.vertical
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MsgChildFragment : BaseFragment<MsgVm, FrMsgchildBinding>() {
    private val mAdapter by lazy { MsgListAdapter() }
    var listdata: MutableList<MsgListNewData> = ArrayList<MsgListNewData>()

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
        mAdapter.addOnItemChildClickListener(R.id.lltDelete) { adapter, view, position ->
            var bean: MsgListNewData? =mAdapter.getItem(position)
            mViewModel.getDelMsg(bean?.anchorId.toString())
            deltDataToList(bean!!)
            mAdapter.removeAt(position)
            if (listdata.size == 0) {
                mAdapter.emptyView = empty
            }
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
                listdata.clear()
                mAdapter.submitList(listdata)
                mAdapter.notifyDataSetChanged()
            }
        }

        getRoomAllData()

    }

    fun getRoomAllData(){
        GlobalScope.launch {
            val data = getAll().await()

            if (data.isNotEmpty()) {
                listdata.clear()
                listdata.addAll(data)
                mAdapter.submitList(listdata)

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

                var chat=ReceiveChatMsg()
                chat.id=it.id
                chat.chatType=it.chatType
                chat.msgType=it.msgType
                chat.cmd=it.cmd
                chat.anchorId=it.anchorId
                chat.content=it.content
                chat.createTime=it.createTime
                chat.from=it.fromId
                chat.sent=it.sent
                chat.groupId=it.groupId
                chat.toAvatar=it.avatar
                chat.toNickName=it.nick
                chat.fromAvatar=it.avatar
                chat.fromNickName=it.nick
                chat.level=it.level
                refshMsg(chat)
            }
        }
    }

    fun updataMsg(list: MutableList<MsgListBean>) {

        var num = 0
        for (i in 0 until list.size) {
            num += list[i].noReadSum
        }
        // appViewModel.updateMainMsgNum.postValue(num)
    }

    override fun createObserver() {

        mViewModel.msgList.observe(this) {
//            if (it.isSuccess) {
//                //成功
//                when {
//                    //第一页并没有数据 显示空布局界面
//                    it.isFirstEmpty -> {
//                        mDatabind.smartCommon.finishRefresh()
//                        mAdapter.emptyView = empty
//                    }
//                    //是第一页
//                    it.isRefresh -> {
//                        mDatabind.smartCommon.finishRefresh()
//                        mDatabind.smartCommon.resetNoMoreData()
//                        listdata.clear()
//                        listdata.addAll(it.listData)
//                        mAdapter.submitList(listdata)
//                        mAdapter.notifyDataSetChanged()
//                        // updataMsg(listdata)
//
//
//                    }
//                    //不是第一页
//                    else -> {
//                        if (it.listData.isEmpty()) {
//                            mDatabind.smartCommon.setEnableLoadMore(false)
//                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
//                        } else {
//                            mDatabind.smartCommon.setEnableLoadMore(true)
//                            mDatabind.smartCommon.finishLoadMore()
//
//                            mAdapter.addAll(it.listData)
//                            // updataMsg(it.listData)
//                        }
//
//                    }
//                }
//            } else {
//                mAdapter.emptyView = empty
//                //失败
//                if (it.isRefresh) {
//                    mDatabind.smartCommon.finishRefresh()
//                    //如果是第一页，则显示错误界面，并提示错误信息
//                    mAdapter.submitList(null)
//                } else {
//                    mDatabind.smartCommon.finishLoadMore(false)
//                }
//            }
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
        addData(msg)

        // updataMsg(listdata)
        var hasMsg = false
        for (i in 0 until listdata.size) {
            if (msg.anchorId == listdata[i].anchorId) {
                hasMsg = true
                var bean = MsgListNewData()
                if (msg.anchorId == msg.from) {//主播发送的消息
                    bean.avatar = msg.fromAvatar!!
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
                bean.fromId = msg.from.toString()
                bean.anchorId = msg.anchorId.toString()
                bean.sent=msg.sent
                bean.id = listdata[i].id

                if (msg.anchorId == msg.from) {//主播发送的消息
                    bean.nick = msg.fromNickName!!
                } else {
                    bean.nick = msg.toNickName!!
                }


                mAdapter[i] = bean//更新Item数据
                mAdapter.swap(i, 0)
                addDataToList(bean)
                break
            }
        }
        if (!hasMsg) {
            var bean = MsgListNewData()
            if (msg.anchorId == msg.from) {//主播发送的消息
                bean.avatar = msg.fromAvatar!!
                if (chatId == msg.anchorId) {
                    bean.noReadSum = 0
                } else {
                    bean.noReadSum = 1
                }
            } else {
                bean.avatar = msg.toAvatar!!
                bean.noReadSum = 0
            }
            bean.content = msg.content.toString()
            bean.createTime = msg.createTime!!
            bean.msgType = msg.msgType!!
            bean.fromId = msg.from!!
            bean.anchorId = msg.anchorId!!
            bean.id = msg.id
            bean.sent=msg.sent
            if (msg.anchorId == msg.from) {//主播发送的消息
                bean.nick = msg.fromNickName!!
            } else {
                bean.nick = msg.toNickName!!
            }

            addDataToList(bean)
            mAdapter.add(0, bean)
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

    fun getAll(): Deferred<List<MsgListNewData>> {
        return GlobalScope.async {
            MyApplication.dataChatList!!.chatDao!!.getAll()
        }
    }

    fun addDataToList(data: MsgListNewData) {
        GlobalScope.launch {
            MyApplication.dataChatList!!.chatDao?.insertOrUpdate(data)


        }
    }
    fun deltDataToList(data: MsgListNewData) {
        GlobalScope.launch {
          var bean=  MyApplication.dataChatList!!.chatDao?.findMessagesById(data.id!!)

            data.idd=bean!!.idd
            //删除会显示在聊天列表的记录数据
            MyApplication.dataChatList!!.chatDao?.delete(data)

            //删除跟这个主播相关的连天记录
            MyApplication.dataBase!!.chatDao?.deleteAllZeroId(data.anchorId!!)

        }
    }

}