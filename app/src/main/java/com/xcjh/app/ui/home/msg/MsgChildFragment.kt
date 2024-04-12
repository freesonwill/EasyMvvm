package com.xcjh.app.ui.home.msg

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.drake.statelayout.StateConfig
import com.xcjh.app.MyApplication
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.databinding.FrMsgchildBinding
import com.xcjh.app.databinding.ItemMsglistBinding
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.ui.feed.FeedNoticeActivity
import com.xcjh.app.bean.MsgBeanData
import com.xcjh.app.bean.MsgListNewData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.ChatTimeUtile
import com.xcjh.app.utils.delMsgDilog
import com.xcjh.app.view.CustomHeader
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * 首页消息fragment
 */
class MsgChildFragment : BaseFragment<MsgVm, FrMsgchildBinding>() {

    var listdata: MutableList<MsgListNewData> = ArrayList<MsgListNewData>()
    var chatId = "-2"
    val empty by lazy { layoutInflater!!.inflate(R.layout.layout_empty, null) }
    var noReadMsgs = 0
    var tags = "MsgChildFragment"

    companion object {

        fun newInstance(): MsgChildFragment {
            val args = Bundle()
            val fragment = MsgChildFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {

        appViewModel.emptychatUserEvent.observeForever { user ->
            if (isAdded) {
                if (mDatabind.rec.mutable != null) {

                    for (i in 0 until mDatabind.rec.mutable.size) {
                        var data = mDatabind.rec.mutable[i] as MsgListNewData
                        if (user.equals(data.anchorId)) {
                            if (data.noReadSum > 0) {//去除红点
                                data.noReadSum = 0
                                addDataToList(data)
                                mDatabind.rec.bindingAdapter.notifyItemChanged(i)
                            }
                        }

                    }



                }

            }

        }


        try {


            mDatabind.rec.run {
                vertical()
                distance(0, 0, 0, 10)
            }
            mDatabind.rec.setup {
                addType<MsgListNewData> {

                    R.layout.item_msglist
                }
                onBind {
                    var binding = getBinding<ItemMsglistBinding>()
                    var item = _data as MsgListNewData

                    when (item?.noReadSum) {

                        0 -> {
                            binding.tvnums1.visibility = View.GONE
                            binding.tvnums2.visibility = View.GONE
                        }

                        in 1..9 -> {
                            binding.tvnums1.visibility = View.VISIBLE
                            binding.tvnums2.visibility = View.GONE
                            binding.tvnums1.text = item!!.noReadSum.toString()
                        }

                        else -> {
                            binding.tvnums1.visibility = View.GONE
                            binding.tvnums2.visibility = View.VISIBLE
                            binding.tvnums2.text = item!!.noReadSum.toString()
                        }

                    }
                    if (item.sent == 2) {//发送失败
                        binding.ivfaile.visibility = View.VISIBLE
                    } else {
                        binding.ivfaile.visibility = View.GONE
                    }
                    if (item.dataType == 2) {//反馈通知
                        binding.tvname.text = context.resources.getString(R.string.txt_feedtype)
                        binding.tvcontent.text = item!!.content
                        Glide.with(context)
                            .load(R.drawable.ic_notify)
                            .placeholder(R.drawable.ic_notify)
                            .into(binding.ivhead)

                    } else {
                        when (item?.msgType) {//	消息类型(0:text、1:image、2:voice、3:video、4:music、5:news)
                            0 -> {
                                binding.tvcontent.text = item!!.content
                            }

                            1 -> {
                                binding.tvcontent.text =
                                    context.resources.getString(R.string.txt_msg_pic)
                            }

                            3 -> {
                                binding.tvcontent.text =
                                    context.resources.getString(R.string.txt_msg_video)
                            }

                            else -> {
                                binding.tvcontent.text = item!!.content
                            }

                        }
                        binding.tvname.text = item!!.nick
                        Glide.with(context).load(item.avatar)
                            .placeholder(R.drawable.default_anchor_icon).into(binding.ivhead)
                    }
                    val time: String = ChatTimeUtile.formatTimestamp(
                        context,
                        item.createTime
                    )!!
                    binding.tvtime.text = time
                    binding.lltDelete.setOnClickListener {
                        delMsgDilog(requireActivity()) { it ->
                            if (it) {//点击了确定
                                mViewModel.getDelMsg(item?.anchorId.toString())
                                deltDataToList(item!!)
                                mDatabind.rec.mutable.removeAt(bindingAdapterPosition)
                                mDatabind.rec.bindingAdapter.notifyItemRemoved(
                                    bindingAdapterPosition
                                )
                                initNoreadMsg(mDatabind.rec.mutable as MutableList<MsgListNewData>)

                                if (mDatabind.rec.models!!.isEmpty()) {
                                    mDatabind.state.showEmpty()
                                }
                            }

                        }

                    }
                    binding.lltItem.setOnClickListener {
                        if (item.noReadSum > 0) {//去除红点
                            item.noReadSum = 0
                            addDataToList(item)
                            notifyItemChanged(bindingAdapterPosition)
                        }

                        if (item?.anchorId == "0") {
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

                    }


                }
            }.models = listdata
//        (mDatabind.rec.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
//            false//防止item刷新的时候闪烁
            mDatabind.smartCommon.setRefreshHeader(CustomHeader(requireContext()))
            mDatabind.state.apply {
                StateConfig.setRetryIds(R.id.ivEmptyIcon, R.id.txtEmptyName)
                onEmpty {
                    this.findViewById<TextView>(R.id.txtEmptyName).text =
                        resources.getString(R.string.nomsg)
                    this.findViewById<ImageView>(R.id.ivEmptyIcon)
                        .setImageDrawable(resources.getDrawable(R.drawable.ic_empety_msg))
                    this.findViewById<ImageView>(R.id.ivEmptyIcon).setOnClickListener { }
                }
            }
            mDatabind.smartCommon.setOnRefreshListener {
                getRoomAllData()

            }.setOnLoadMoreListener {
                //  mViewModel.getMsgList(false, "")
            }


            initEvent()
            //登录或者登出
            appViewModel.updateLoginEvent.observe(this) {
                if (it) {
                    mViewModel.getUserInfo()

                    // initEvent()
                } else {
                    // MyWsManager.getInstance(requireActivity())!!.removeC2CListener(tags)
                    listdata.clear()
                    mDatabind.rec.models = mutableListOf()
                    initNoreadMsg(listdata)

                }
            }

            appViewModel.appMsgResum.observe(this) {
                if (it) {
                    if (CacheUtil.isLogin()) {
                        getRoomAllData()

                    }
                }
            }
        } catch (e: Exception) {
        }
    }


    fun initNoreadMsg(data: MutableList<MsgListNewData>) {
        try {


            if (data.isNotEmpty()) {
                noReadMsgs = 0
                for (i in 0 until data.size) {
                    if (data[i].noReadSum > 0) {
                        noReadMsgs += data[i].noReadSum

                    }
                }
                appViewModel.updateMainMsgNum.postValue(noReadMsgs.toString())

            } else {
                appViewModel.updateMainMsgNum.postValue("0")
            }
        } catch (e: Exception) {
        }
    }

    fun getRoomAllData(isGet: Boolean = true) {
        try {
            if (CacheUtil.getUser() == null) {
                mViewModel.getUserInfo()
                return
            }
            GlobalScope.launch {
                val data = getAll().await()
                requireActivity().runOnUiThread {
                    if (data.isNotEmpty()) {
                        noReadMsgs = 0
                        listdata.clear()
                        listdata.addAll(data)
                        initNoreadMsg(data as MutableList<MsgListNewData>)
                        mDatabind.rec.models = listdata
                        mDatabind.state.showContent()

                    } else {
                        mDatabind.state.showEmpty()
                    }
                    if (isGet) {

                        mViewModel.getMsgList(true, "")
                    }
                }

            }
        } catch (e: Exception) {
        }
    }


    override fun onResume() {
        super.onResume()
        if (CacheUtil.isLogin()) {
            getRoomAllData()

        }
    }

    private fun initEvent() {
        try {


            MyWsManager.getInstance(requireActivity())!!
                .setC2CListener(tags, object : C2CListener {
                    override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {
                        if (isOk) {

                        }
                    }

                    override fun onSystemMsgReceive(it: FeedSystemNoticeBean) {
                        //系统反馈通知推送消息
                        var chat = ReceiveChatMsg()
                        chat.id = it.noticeId.toString()
                        chat.msgType = 1
                        chat.content = it.title
//                    if (it.reason==null)//取消禁言
//                    {
//                        chat.content = it.title
//                    } else {//禁言
//                        chat.content = it.reason!!
//                    }

                        chat.createTime = it.createTime
                        chat.sent = 1
                        chat.dataType = 2
                        chat.toAvatar = ""
                        chat.from = "0"
                        chat.anchorId = "0"
                        chat.noReadSum = 1
                        chat.toNickName = ""
                        chat.fromAvatar = ""
                        chat.fromNickName = ""
                        refshMsg(chat, true)

                    }

                    override fun onC2CReceive(chat: ReceiveChatMsg) {

                        if (chat.from != CacheUtil.getUser()?.id) {//收到消息
                            var beanmy: MsgBeanData = MsgBeanData()
                            beanmy.anchorId = chat.anchorId
                            beanmy.fromId = chat.from
                            beanmy.content = chat.content
                            beanmy.id = chat.id
                            beanmy.chatType = chat.chatType
                            beanmy.sendId = chat.sendId
                            beanmy.cmd = 11
                            beanmy.msgType = chat.msgType
                            beanmy.createTime = chat.createTime

//                            addDataToChatList(beanmy)

                        }
                        if (chatId != chat.anchorId) {
                            chat.noReadSum = 1
                        }
                        refshMsg(chat, true)
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
                    chat.sent = it.sentNew
                    chat.groupId = it.groupId
                    chat.toAvatar = it.avatar
                    chat.toNickName = it.nick
                    chat.fromAvatar = it.avatar
                    chat.fromNickName = it.nick
                    chat.level = it.level
                    refshMsg(chat)
                }
            }
        } catch (e: Exception) {
        }
    }


    /***
     * 返回后一个时间戳是否比前一个时间戳早的布尔值
     */
    private fun isTimestampEarlier(timestamp1: Long, timestamp2: Long): Boolean {
        return timestamp2 < timestamp1
    }

    fun updataMsg(it: MsgListNewData) {
        try {


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
                chat.from = "0"
                chat.anchorId = "0"
            } else {

                chat.from = it.anchorId
                chat.anchorId = it.anchorId
            }
            chat.noReadSum = it.noReadSum
            chat.toNickName = it.nick
            chat.fromAvatar = it.avatar
            chat.fromNickName = it.nick
            refshMsg(chat)
        } catch (e: Exception) {
        }
    }

    override fun createObserver() {
        try {


            mViewModel.upUserInfo.observe(this) {
                getRoomAllData()


            }
            mViewModel.msgList.observe(this) {
                //获取数据
                mDatabind.smartCommon.finishRefresh()
                if (it.isSuccess) {

                    if (it.listData.size > 0) {
                        for ((index, data) in it.listData.withIndex()) {

                            val foundData = listdata.find { it.anchorId == data.anchorId }
                            if (foundData == null) {
                                data?.let { it1 ->
                                    //if (it1.noReadSum > 0) {//这里看看需不需要判断  因为色剂到 多设备登录的问题
                                    if (it1.sendId == "0") {
                                        it1.sendId = data.anchorId + it1.createTime
                                    }
                                    updataMsg(data)
                                    //}
                                }
                            } else {
                                if (data.noReadSum > foundData.noReadSum ) {
                                    updataMsg(data)
                                }else if(data.dataType==2&&data.content!=foundData.content){
                                    //因为是系统的语言~有可能是其他语言所以加了一个判断如果不是相等就保存一下
                                    updataMsg(data)
                                }

                                else {
                                    if (data.dataType == 1 && data.avatar != null && data.avatar!!.isNotEmpty() && data.nick != null && data.nick!!.isNotEmpty()) {
                                        if (data.avatar != foundData.avatar || data.nick != foundData.nick||data.content != foundData.content) {
                                            updataMsg(data)
                                        }
                                    }
                                }
                            }
                        }

                    } else {

                    }


                }
            }
            mViewModel.clreaAllMsg.observe(this) {
                noReadMsgs = 0
                appViewModel.updateMainMsgNum.postValue("0")

                for (i in 0 until listdata.size) {
                    if (listdata[i].noReadSum > 0) {
                        listdata[i].noReadSum = 0
                        addDataToList(listdata[i])
                    }
                }
                getRoomAllData()


                // mViewModel.getMsgList(true, "")
            }
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        MyWsManager.getInstance(requireActivity())!!.removeC2CListener(tags)
        super.onDestroy()
    }

    fun refshMsg(msg: ReceiveChatMsg, isAdd: Boolean = false) {

        try {

            var hasMsg = false
            for (i in 0 until listdata.size) {
                if (msg.anchorId == listdata[i].anchorId) {
                    hasMsg = true
                    var bean = MsgListNewData()
                    if (CacheUtil.getUser()?.id != msg.from) {//主播发送的消息
                        bean.avatar = msg.fromAvatar ?: ""
                        if (chatId == msg.anchorId) {
                            bean.noReadSum = 0

                        } else {
                            var count = listdata[i].noReadSum + 1
                            if (msg.noReadSum > count) {

                                bean.noReadSum = msg.noReadSum
                            } else if (isAdd) {

                                bean.noReadSum = listdata[i].noReadSum + 1
                            } else {

                                bean.noReadSum = msg.noReadSum

                            }

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

                    if (CacheUtil.getUser()?.id != msg.from) {//主播发送的消息
                        bean.nick = if (msg.fromNickName == null) "" else msg.fromNickName!!
                    } else {
                        bean.nick = if (msg.toNickName == null) "" else msg.toNickName!!
                    }

                    addDataToList(bean)
                    break
                }
            }
            if (!hasMsg) {
                var bean = MsgListNewData()
                if (CacheUtil.getUser()?.id != msg.from) {//主播发送的消息
                    bean.avatar = msg.fromAvatar ?: ""
                    if (chatId == msg.anchorId) {
                        bean.noReadSum = 0
                    } else {
                        if (msg.noReadSum == 0) {
                            bean.noReadSum = 0

                        } else {

                            bean.noReadSum = msg.noReadSum
                        }

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
                if (CacheUtil.getUser()?.id != msg.from) {//主播发送的消息
                    bean.nick = msg.fromNickName ?: ""
                } else {
                    bean.nick = msg.toNickName ?: ""
                }


                addDataToList(bean)


            }
        } catch (e: Exception) {
            LogUtils.d("出错了" + e.printStackTrace())
        }


    }


    /***
     * 索取所有消息缓存
     */
    fun getAll(): Deferred<List<MsgListNewData>> {
        return GlobalScope.async {
            MyApplication.dataChatList!!.chatDao!!.getAll(CacheUtil.getUser()?.id!!)
        }
    }

    /***
     * 添加或者更新私聊新的数据
     */
    fun addDataToChatList(data: MsgBeanData) {

//        if (CacheUtil.getUser() != null) {
//            data.withId = CacheUtil.getUser()?.id!!
//            GlobalScope.launch {
//                MyApplication.dataBase!!.chatDao?.insertOrUpdate(data)
//            }
//        }
    }

    /***
     * 添加或者更新新的数据
     */
    fun addDataToList(data: MsgListNewData) {
        data.withId = CacheUtil.getUser()?.id!!
        GlobalScope.launch {
            MyApplication.dataChatList!!.chatDao?.insertOrUpdate(data)

            getRoomAllData(false)

        }
    }

    /***
     * 删除数据
     */
    fun deltDataToList(data: MsgListNewData) {
        GlobalScope.launch {
            var bean =
                MyApplication.dataChatList!!.chatDao?.findMessagesById(data.anchorId!!)

            data.idd = bean!!.idd
            //删除会显示在聊天列表的记录数据
            MyApplication.dataChatList!!.chatDao?.delete(data)

            //删除跟这个主播相关的连天记录
//            MyApplication.dataBase!!.chatDao?.deleteAllZeroId(data.anchorId!!)


        }
    }

//    /***
//     * 添加或者更新新的数据
//     */
//    fun delAllRoom() {
//        GlobalScope.launch {
//            MyApplication.dataChatList!!.clearAllTables()
//            MyApplication.dataBase!!.clearAllTables()
//        }
//    }
}