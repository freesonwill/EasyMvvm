package com.xcjh.app.ui.details.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.layoutmanager.HoverLinearLayoutManager
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.softinput.hideSoftInput
import com.google.gson.Gson
import com.just.agentweb.AgentWeb
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.*
import com.xcjh.app.databinding.*
import com.xcjh.app.isTopActivity
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.common.RoomChatVm
import com.xcjh.app.utils.*
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.FeedSystemNoticeBean
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.bean.SendChatMsgBean
import com.xcjh.app.websocket.bean.UpdateLiveContentBean
import com.xcjh.app.websocket.listener.LiveRoomListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.utils.dip2px
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.toHtml
import com.xcjh.base_lib.utils.view.visibleOrGone
import kotlinx.android.synthetic.main.fragment_detail_tab_chat.view.*


/**
 * 聊天 反向布局
 */

class DetailChat2Fragment(var liveId: String, var userId: String?, override val typeId: Long = 1) :
    BaseVpFragment<RoomChatVm, FragmentDetailTabChat2Binding>(),
    LiveRoomListener, View.OnClickListener {
    private val req: HistoryMsgReq = HistoryMsgReq("", "", "")//只为获取分页size
    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }
    private var mAgentWeb: AgentWeb? = null
    private var mNoticeWeb: WebView? = null
    private var isEnterRoom = false//是否已经进入房间
    private var isInitNoticeH = true//是否需要初始化公告高度
    private val noticeBean by lazy {
        NoticeBean(
            //notice = getString(R.string.anchor_notice),
            notice = "",
            itemHover = true
        )
    }

    private val mLayoutManager by lazy {
        HoverLinearLayoutManager(context, RecyclerView.VERTICAL, true).apply {
            stackFromEnd = true
        }
    }

    private var offset = ""



    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.v = this
        mDatabind.m = mViewModel
        setNotice()
        initRcv()
        getHistoryData()
        handleSoftInput(requireActivity(), mDatabind.llInput)
        if (CacheUtil.isLogin()&&(userId!=null&&!userId.equals("")) ) {
            mViewModel.getAnchorControlUserInfo(userId!!)
        }





//        setProhibition(true)
//        blacklistDilog(requireContext(),1)
    }

    /**
     * 是否禁言true是禁言center
     */
    fun setProhibition(isflage:Boolean){
        if(isflage){

            mDatabind.txtProhibit.visibility=View.VISIBLE

//            mDatabind.edtChatMsg.isFocusable=false
            mDatabind.edtChatMsg.setText("")
//            mDatabind.edtChatMsg.hint=resources.getString(R.string.str_stoptalk)
//            mDatabind.edtChatMsg.gravity= Gravity.CENTER
//            mDatabind.sendChat.isEnabled=false
//            mDatabind.sendChat.visibility=View.GONE
        }else{
            mDatabind.txtProhibit.visibility=View.GONE
            mDatabind.edtChatMsg.setText("")
//            mDatabind.edtChatMsg.hint=resources.getString(R.string.say_something)
//            mDatabind.edtChatMsg.isFocusable = true
//            mDatabind.edtChatMsg.isFocusableInTouchMode = true
//            mDatabind.edtChatMsg.inputType = InputType.TYPE_CLASS_TEXT // 或者其他你需要的输入类型
//            mDatabind.sendChat.isEnabled=true
//            mDatabind.sendChat.visibility=View.VISIBLE
//            mDatabind.edtChatMsg.gravity= Gravity.LEFT or Gravity.CENTER

        }

    }

    private fun setNotice() {
        isInitNoticeH = true
        if (!userId.isNullOrEmpty()) {
            mDatabind.notice.root.visibility = View.VISIBLE
            mDatabind.notice.apply {
                mNoticeWeb = agentWeb
                setWeb(mNoticeWeb!!) {
                    mDatabind.lltNotice.postDelayed({
                        if (isAdded && !noticeBean.isOpen && isInitNoticeH) {
                          /*  val params = mDatabind.page.layoutParams as RelativeLayout.LayoutParams
                            params.topMargin = mDatabind.lltNotice.height
                            mDatabind.page.layoutParams = params
                            val params2 = mDatabind.rcvChat.layoutParams
                            params2.height = mDatabind.page.height
                            mDatabind.rcvChat.layoutParams = params2*/
                        }
                        isInitNoticeH = false
                    }, 200)
                }
                noticeBean.isOpen=false
                tvArrow.text = if (noticeBean.isOpen) getString(R.string.pack_up) else getString(R.string.expand)
                startImageRotate(expandCollapse, noticeBean.isOpen)
                lltExpandCollapse.setOnClickListener {
                    noticeBean.isOpen = !noticeBean.isOpen
                    tvArrow.text =
                        if (noticeBean.isOpen) getString(R.string.pack_up) else getString(R.string.expand)
                    startImageRotate(expandCollapse, noticeBean.isOpen)
                    if (noticeBean.isOpen) {
                        setH5Data(mNoticeWeb, noticeBean.notice, tvColor = "#94999f", maxLine = 10)
                    } else {
                        setH5Data(mNoticeWeb, noticeBean.notice, tvColor = "#94999f", maxLine = 2)
                    }
                }
            }
        } else {
            mDatabind.notice.root.visibility = View.GONE
            val params = mDatabind.page.layoutParams as RelativeLayout.LayoutParams
            params.topMargin = 0
            mDatabind.page.layoutParams = params
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRcv() {
        /*  mDatabind.page.setEnableRefresh(false)
          mDatabind.page.setEnableLoadMore(true)*/
        mDatabind.page.setEnableOverScrollBounce(false)
        mDatabind.page.preloadIndex = 20
        ClassicsFooter.REFRESH_FOOTER_NOTHING = ""
        mDatabind.page.emptyLayout = R.layout.layout_empty
        mDatabind.page.stateLayout?.onEmpty {
            val emptyImg = findViewById<ImageView>(R.id.ivEmptyIcon)
            val emptyHint = findViewById<TextView>(R.id.txtEmptyName)
            val lltContent = findViewById<LinearLayout>(R.id.lltContent)
            val lp = lltContent.layoutParams as RelativeLayout.LayoutParams
            lp.topMargin = dp2px(60)
            emptyImg.setOnClickListener {}
            emptyImg.setImageResource(R.drawable.ic_empty_detail_chat)
            emptyHint.text = "暂无聊天内容"
            emptyHint.setTextColor(context.getColor(R.color.c_5b5b5b))
        }
        mDatabind.page.onRefresh {
            // mViewModel.getHisMsgList(liveId, offset)
            vm.getMsgHistory("", liveId, offset, false)
            "onRefresh".loge("888====")
        }
        setChatRoomRcv(vm, mDatabind.rcvChat, mLayoutManager, true, {
            mAgentWeb = it
        }, {
            mDatabind.rcvChat.postDelayed({
                if (isAdded) {
                    val params = mDatabind.rcvChat.layoutParams
                    params.height = mDatabind.page.height
                    mDatabind.rcvChat.layoutParams = params
                    mDatabind.rcvChat.scrollToPosition(0)
                }
            }, 200)

        })
        //点击列表隐藏软键盘
        mDatabind.rcvChat.setOnTouchListener { v, _ ->
            v.clearFocus() // 清除文字选中状态
            hideSoftInput() // 隐藏键盘
            mDatabind.edtChatMsg.clearFocus()
            false
        }
        mDatabind.edtChatMsg.isEnabled = false
        mDatabind.edtChatMsg.postDelayed({
            try {
                if (isAdded) {
                    mDatabind.edtChatMsg.isEnabled = true
                }
            } catch (e: Exception) {
                // e.message?.loge("7888===")
            }
        }, 800)
        //点击列表隐藏软键盘
        mDatabind.edtChatMsg.setOnFocusChangeListener { v, hasFocus ->
            // setWindowSoftInput(float = mDatabind.llInput, setPadding = true)
            if (hasFocus) {
                judgeLogin()
            }
        }
        mDatabind.edtChatMsg.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMsg()
            }
            true
        }
    }

    private fun getHistoryData() {
        vm.getMsgHistory(userId, liveId, offset, true)
        // mViewModel.getHisMsgList(liveId, offset, true)
    }

    override fun createObserver() {
        //获取当前主播是否禁言或者踢出了这个用户
        mViewModel.anchorControl.observe(this){
            //被踢出
            if(it.tickOut){
                //进入直播间查询被踢出
                blacklistDilog(requireContext(),1)
            }else if(it.mute){//被禁言

                setProhibition(true)
            }
        }

        var  num=0
        //公告
        vm.anchor.observe(this) {
            if (it != null) {
//                if(num!=0){
//                    it.notice= "<p><span style=\"color: rgb(225, 60, 57);\">欢迎观众的到来，感谢大家对本直播间的支持和关注</span></p><p><span style=\"color: rgb(114, 192, 64);\">为了营造良好的观赛氛围，本直播间禁止任何形式的恶意言论、人身攻击和不文明行为。</span></p>"
//                }
//                num=2


                noticeBean.notice = it.notice ?: ""
                noticeBean.isOpen = false
                mDatabind.notice.expandableText.text =
                    noticeBean.notice.replace("<p>", "<span>").replace("</p>", "</span>").toHtml()
                "${mDatabind.lltNotice.height}".loge("====999==")
                val layout = mDatabind.notice.expandableText.layout
                if (layout != null) {
                    val lineCount: Int = layout.lineCount
                    lineCount.toString().loge("888===")
                    mDatabind.notice.lltExpandCollapse.visibleOrGone(lineCount > 2)
                    //动态设置TextView距离顶部的间距 如果要设置主播公告盖住
                    val params = mDatabind.page.layoutParams as RelativeLayout.LayoutParams
                    params.setMargins(0,if (lineCount==1){
                        dip2px(88f)
                    }else{
                        dip2px(110f)
                    },0,0)
                    mDatabind.page.layoutParams = params

                    //mDatabind.notice.expandableText.maxLines =  2
                }
                setH5Data(mNoticeWeb, noticeBean.notice, tvColor = "#94999f", maxLine = 2)
            }
        }

        //修改公告公告
        vm.anchorUpdate.observe(this) {
            if (it != null) {
//                if(num!=0){
//                    it.notice= "<p><span style=\"color: rgb(225, 60, 57);\">1111111111</span></p><p><span style=\"color: rgb(114, 192, 64);\">为了营造良好的观赛氛围，本直播间禁止任何形式的恶意言论、人身攻击和不文明行为。</span></p>"
//                }
//                num=2


                noticeBean.notice = it.notice ?: ""
                noticeBean.isOpen = false
                mDatabind.notice.expandableText.text =
                    noticeBean.notice.replace("<p>", "<span>").replace("</p>", "</span>").toHtml()
                "${mDatabind.lltNotice.height}".loge("====999==")
                val layout = mDatabind.notice.expandableText.layout
                if (layout != null) {
                    val lineCount: Int = layout.lineCount
                    lineCount.toString().loge("888===")
                    mDatabind.notice.lltExpandCollapse.visibleOrGone(lineCount > 2)
                    //动态设置TextView距离顶部的间距
                    val params = mDatabind.page.layoutParams as RelativeLayout.LayoutParams
                    params.setMargins(0,if (lineCount==1){
                        dip2px(88f)
                    }else{
                        dip2px(110f)
                    },0,0)
                    mDatabind.page.layoutParams = params

                    //mDatabind.notice.expandableText.maxLines =  2
                }
                setH5Data(mNoticeWeb, noticeBean.notice, tvColor = "#94999f", maxLine = 2)
            }
        }

        //历史消息
        vm.hisMsgList.observe(this) {
            mDatabind.page.finishRefresh()
            if (it.isSuccess) {
                if (it.listData.isEmpty()) {
                    //mDatabind.smartChat.setEnableRefresh(false)
                    if (it.isRefresh && userId.isNullOrEmpty()) {
                        mDatabind.page.showEmpty()
                    } else {
                        mDatabind.page.showContent(false)
                    }
                } else {
                    mDatabind.page.showContent(true)
                    mDatabind.page.finish(
                        true,
                        it.listData.size == req.size + if (it.isRefresh) 1 else 0
                    )
                    mDatabind.rcvChat.addModels(it.listData) // 添加一条消息
                    val last = it.listData.last()
                    if (last is MsgBean) {
                        offset = last.id ?: ""
                    }
                    if (it.isRefresh) {
                        mDatabind.rcvChat.scrollToPosition(0)
                    }
                }
            } else {
                if (it.isRefresh && userId.isNullOrEmpty()) {
                    mDatabind.rcvChat.addModels(null)
                    mDatabind.page.showEmpty()
                } else {
                    mDatabind.page.showContent()
                }
            }
        }
        vm.anchorInfo.observe(this) {
            updateChatRoom(it.liveId, it.userId)
        }

        appViewModel.wsStatusOpen.observe(this) {
            if (isAdded && it) {
                //断开后重连成功，重新进入房间
                MyWsManager.getInstance(App.app)?.setLiveRoomListener(activity.toString(), this)
                onWsUserEnterRoom(liveId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MyWsManager.getInstance(App.app)?.setLiveRoomListener(activity.toString(), this)
        onWsUserEnterRoom(liveId)
    }

    override fun onResume() {
        super.onResume()
        mDatabind.page.postDelayed({
            if (isAdded) {
                val params = mDatabind.rcvChat.layoutParams
                params.height = mDatabind.page.height
                mDatabind.rcvChat.layoutParams = params
            }
        }, 200)
        mNoticeWeb?.onResume()
        mAgentWeb?.webLifeCycle?.onResume()
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
        if (mViewModel.input.get().isBlank()) {
            mViewModel.input.set("")
        }
        mDatabind.edtChatMsg.clearFocus()
        mNoticeWeb?.onPause()
        mAgentWeb?.webLifeCycle?.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (!isTopActivity(activity)) {
            // exitRoom()
        }
        //hideSoftInput()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        clearWebView(mNoticeWeb)
        exitRoom()
        super.onDestroy()
    }

    private fun exitRoom() {
        onWsUserExitRoom(liveId)
        MyWsManager.getInstance(App.app)?.removeLiveRoomListener(activity.toString())

        isEnterRoom = false
    }

    override fun onEnterRoomInfo(isOk: Boolean, msg: ReceiveWsBean<*>) {
        isEnterRoom = true
    }

    override fun onExitRoomInfo(isOk: Boolean, msg: ReceiveWsBean<*>) {
        // myToast("exit Room ==$isOk")
    }

    override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {
        //myToast("send_msg ==$isOk")
    }

    //被踢出直播间
    override fun onIsBlacklist(feed: FeedSystemNoticeBean) {
        if(userId!!.isNotEmpty()){
            if(liveId!!.equals(feed.bizId)){
                blacklistDilog(requireContext(),2)
            }
        }

    }

    /**
     * 更新直播间公告
     */
    override fun onUpdateLiveContent(content: UpdateLiveContentBean) {
        if(userId!!.isNotEmpty()){
            if(liveId!!.equals(content.liveId)){
                vm.getDetailAnchorInfo(userId)
            }

        }
    }


    //禁言
    override fun onProhibition(feed: FeedSystemNoticeBean) {

        if(userId!!.isNotEmpty()){
            if(feed.bizId.equals(userId)){
                myToast(resources.getString(R.string.no_chat_t))
                setProhibition(true)
            }
        }

    }
    //解禁
    override fun onOpeningUp(feed: FeedSystemNoticeBean) {
        if(userId!!.isNotEmpty()){
            if(feed.bizId.equals(userId)){
                setProhibition(false)
            }
        }
    }


    override fun onRoomReceive(chat: ReceiveChatMsg) {
        var isShowBottom = false
        val firstVisible: Int = mLayoutManager.findFirstVisibleItemPosition()
        var lastVisible: Int = mLayoutManager.findLastVisibleItemPosition()
        if (chat.groupId != liveId) {
            return
        }
        mDatabind.page.showContent()
        mDatabind.rcvChat.models?.apply {
            if (this.size > 3 && firstVisible < 1) {
                isShowBottom = true
            }
        }
        mDatabind.rcvChat.addModels(
            listOf(
                MsgBean(
                    chat.from,
                    chat.fromAvatar,
                    chat.fromNickName ?: "",
                    chat.level,
                    chat.content,
                    msgType = chat.msgType,
                    identityType = chat.identityType,
                )
            ), index = 0
        ) // 添加一条消息

        if (chat.from == CacheUtil.getUser()?.id || isShowBottom) {
            mDatabind.rcvChat.scrollToPosition(0)
        }
    }




    override fun onClick(v: View?) {
        when (v) {
            mDatabind.sendChat -> {
                sendMsg()
            }
        }
    }

    private var lastClickTime = 0L
    private fun sendMsg() {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime < 1000)) {
            return
        }
        lastClickTime = currentTime
        if (mViewModel.input.get().isBlank() || mViewModel.input.get().isEmpty()) {
            myToast("请输入内容", isDeep = true)
            return
        }
        hideSoftInput()
        mDatabind.edtChatMsg.clearFocus()
        judgeLogin {
            CacheUtil.getUser()?.apply {
                MyWsManager.getInstance(App.app)?.sendMessage(
                    Gson().toJson(SendChatMsgBean(
                        1, 0, 11,
                        from = id,
                        fromAvatar = head,
                        fromNickName = name,
                        content = mViewModel.input.get(),
                        identityType = "0",
                        createTime = System.currentTimeMillis(),
                        level = lvNum,
                        groupId = liveId,
                    ).apply {
                    })
                )
            }
            mViewModel.input.set("")
        }
        /* mDatabind.sendChat.postDelayed({

         }, 400)*/
    }

    /**
     * 切换直播间  userId=null就是纯净流
     */
    private fun updateChatRoom(liveId: String, userId: String?) {
        onWsUserExitRoom(this.liveId)
        this.liveId = liveId
        this.userId = userId


        offset = ""
        setNotice()
        mDatabind.page.resetNoMoreData()
        mDatabind.rcvChat.postDelayed({
            //布局重新计算
            if (isAdded) {
                val params = mDatabind.rcvChat.layoutParams
                params.height = mDatabind.page.height
                mDatabind.rcvChat.layoutParams = params
                mDatabind.rcvChat.models = arrayListOf()
                mDatabind.page.showContent()
                onWsUserEnterRoom(liveId)
                getHistoryData()
            }
        }, 200)

    }



    /***
     * 是黑名单 1是进入直播间  2是在直播间的时候被退出
     */
    fun blacklistDilog(context: Context, type:Int) {
        CustomDialog.build()
            .setCustomView(object : OnBindView<CustomDialog?>(R.layout.layout_dialogx_delmsg) {
                override fun onBind(dialog: CustomDialog?, v: View) {
                    val tvcancle = v.findViewById<TextView>(R.id.tvcancle)
                    val textName = v.findViewById<TextView>(R.id.textName)
                    val tvsure = v.findViewById<TextView>(R.id.tvsure)
                    val viewGen = v.findViewById<View>(R.id.viewGen)
                    if(type==2){
                        textName.text=resources.getString(R.string.live_txt_remove)
                    }else{
                        textName.text=resources.getString(R.string.live_txt_retry)

                    }

                    tvcancle.visibility=View.GONE
                    viewGen.visibility=View.GONE
                    tvsure.setOnClickListener {
                        activity!!.finish()
                        dialog?.dismiss()

                    }
                }
            }).setAlign(CustomDialog.ALIGN.CENTER).setCancelable(false).
            setMaskColor(//背景遮罩
                ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)

            ).show()
    }
}

