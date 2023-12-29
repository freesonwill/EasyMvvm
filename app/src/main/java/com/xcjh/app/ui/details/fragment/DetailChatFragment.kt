package com.xcjh.app.ui.details.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.layoutmanager.HoverLinearLayoutManager
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.softinput.hideSoftInput
import com.google.gson.Gson
import com.just.agentweb.AgentWeb
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
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.bean.SendChatMsgBean
import com.xcjh.app.websocket.listener.LiveRoomListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.toHtml
import com.xcjh.base_lib.utils.view.visibleOrGone
import kotlinx.android.synthetic.main.fragment_detail_tab_chat.view.*


/**
 * 聊天 正向布局
 */

class DetailChatFragment(
    private var liveId: String,
    var userId: String?,
    override val typeId: Long = 1,
) :
    BaseVpFragment<RoomChatVm, FragmentDetailTabChatBinding>(),
    LiveRoomListener, View.OnClickListener {

    private val vm by lazy {
        ViewModelProvider(requireActivity())[DetailVm::class.java]
    }

    private var isEnterRoom = false//是否已经进入房间
    private val noticeBean by lazy {
        NoticeBean(
            //notice = getString(R.string.anchor_notice),
            notice = "",
            itemHover = true
        )
    }

    private val mLayoutManager by lazy {

        HoverLinearLayoutManager(context, RecyclerView.VERTICAL, false).apply {
            // stackFromEnd = true
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
    }

    private fun setNotice() {
        if (!userId.isNullOrEmpty()) {
            mDatabind.notice.root.visibility = View.VISIBLE
            mDatabind.notice.apply {
                expandableText.movementMethod = LinkMovementMethod.getInstance()
                expandableText.text = noticeBean.notice
                lltExpandCollapse.setOnClickListener {
                    val aa = expandableText.height
                    noticeBean.isOpen = !noticeBean.isOpen
                    tvArrow.text =
                        if (noticeBean.isOpen) getString(R.string.pack_up) else getString(R.string.expand)
                    startImageRotate(expandCollapse, noticeBean.isOpen)
                    expandableText.maxLines = if (noticeBean.isOpen) 20 else 2
                    mDatabind.rcvChat.postDelayed({
                        val bb = expandableText.height
                        val params = mDatabind.rcvChat.layoutParams
                        params.height = mDatabind.rcvChat.height - bb + aa
                        mDatabind.rcvChat.layoutParams = params
                    }, 200)

                }
            }
        } else {
            mDatabind.notice.root.visibility = View.GONE
        }
    }
    private var mAgentWeb: AgentWeb?=null
    @SuppressLint("ClickableViewAccessibility")
    private fun initRcv() {
        mDatabind.smartChat.setEnableLoadMore(false)
        mDatabind.smartChat.setEnableOverScrollBounce(false)
        mDatabind.smartChat.emptyLayout = R.layout.layout_empty
        mDatabind.smartChat.stateLayout?.onEmpty {
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
        mDatabind.smartChat.setOnRefreshListener {
            if (!mDatabind.rcvChat.models.isNullOrEmpty() && offset.isEmpty()){
                mDatabind.smartChat.finishRefresh(1000)
            }else{
                mViewModel.getHisMsgList(liveId, offset)
            }
        }
        setChatRoomRcv(mDatabind.rcvChat,mLayoutManager,false,{
            mAgentWeb=it
        }){
            offset = it?: ""
        }
        val defaultItemAnimator = DefaultItemAnimator()
        //  val defaultItemAnimator = MyItemAnimator()
        // val defaultItemAnimator = SlideInLeftAnimator()
        defaultItemAnimator.addDuration = 500
        mDatabind.rcvChat.itemAnimator = defaultItemAnimator
        //点击列表隐藏软键盘
        mDatabind.rcvChat.setOnTouchListener { v, _ ->
            v.clearFocus() // 清除文字选中状态
            hideSoftInput() // 隐藏键盘
            mDatabind.edtChatMsg.clearFocus()
            false
        }
        mDatabind.edtChatMsg.isEnabled=false
        mDatabind.edtChatMsg.postDelayed({
            mDatabind.edtChatMsg.isEnabled=true
        }, 1000)
        mDatabind.edtChatMsg.setOnFocusChangeListener { v, hasFocus ->
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
        mViewModel.getHisMsgList(liveId, offset, true)
    }

    override fun createObserver() {
        //公告
        vm.anchor.observe(this) { it ->
            if (it != null) {
                noticeBean.notice = it.notice ?: ""

               /*  val richText = ("<font color=\"red\">红色样式</font><br />"
                         + "<big>大号字样式</big><br />"
                         + "<small>小号字样式</small><br />"
                         + "<i>斜体样式</i><br />"
                         + "<b>粗体样式</b><br />"
                         + "<tt>等t宽t样式</tt><br />"
                         + "<p>段落样式</p><br />"
                         +"<img src=\"https://5b0988e595225.cdn.sohucs.com/images/20180615/0338b5602889474d9935ec4214de9695.jpeg\" title=\"abcjpg\">"
                         + "<font color='#F7DA73'><a href =\"http://www.baidu.com\">百度一下</a></font>")*/
               /*  richText.toHtml {
                     Handler(Looper.getMainLooper()).post {

                         mDatabind.notice.expandableText.text = it
                     }
                 }*/

                mDatabind.notice.expandableText.text =  it.notice?.toHtml() //主播公告
                val lineCount: Int = mDatabind.notice.expandableText.layout.lineCount
                mDatabind.notice.lltExpandCollapse.visibleOrGone(lineCount > 2)
                if (lineCount > 2) {
                    // 内容超过了两行
                    mDatabind.notice.expandableText.maxLines =  2
                } else {
                    // 内容没有超过两行
                }

                mDatabind.rcvChat.postDelayed({
                    try {
                        val params = mDatabind.rcvChat.layoutParams
                        params.height = mDatabind.smartChat.height
                        mDatabind.rcvChat.layoutParams = params
                        mDatabind.rcvChat.addModels(
                            listOf(
                                FirstMsgBean(
                                    it.id,
                                    it.head,
                                    it.nickName,
                                    "0",
                                    //richText,
                                    it.firstMessage ?: "",
                                    identityType = 1
                                ),
                                BottomMsgBean()
                            ),
                            // index = 0
                        ) // 添加一条消息
                        mDatabind.rcvChat.models?.size?.let {
                            mDatabind.rcvChat.smoothScrollToPosition(it)
                        }
                    } catch (_: Exception) {
                    }
                }, 500)
            }
        }
        //历史消息
        mViewModel.hisMsgList.observe(this) {
            mDatabind.smartChat.finishRefresh()
            if (it.isSuccess) {
                if (it.listData.isEmpty()) {
                    //mDatabind.smartChat.setEnableRefresh(false)
                    mDatabind.smartChat.finishRefreshWithNoMoreData()
                    if (it.isRefresh && userId.isNullOrEmpty()) {
                        mDatabind.smartChat.showEmpty()
                    }
                } else {
                    mDatabind.smartChat.showContent()
                    mDatabind.rcvChat.addModels(it.listData, index = 0) // 添加一条消息
                    if (it.isRefresh) {
                        mDatabind.rcvChat.smoothScrollToPosition(
                            mDatabind.rcvChat.models?.size ?: 0
                        )
                    }
                }
            } else {
                if (it.isRefresh && userId.isNullOrEmpty()) {
                    mDatabind.rcvChat.addModels(null)
                    mDatabind.smartChat.showEmpty()
                }
            }
        }
        vm.anchorInfo.observe(this) {
            updateChatRoom(it.liveId, it.userId)
        }
        appViewModel.wsStatus.observe(this) {
            if (isAdded && it == 1) {
                //断开后重连成功，重新进入房间
                MyWsManager.getInstance(App.app)?.setLiveRoomListener(activity.toString(), this)
                onWsUserEnterRoom(liveId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
       /* if (!isEnterRoom) {
            MyWsManager.getInstance(App.app)?.setLiveRoomListener(activity.toString(), this)
            onWsUserEnterRoom(liveId)
        }*/
        MyWsManager.getInstance(App.app)?.setLiveRoomListener(activity.toString(), this)
        onWsUserEnterRoom(liveId)
        activity.toString().loge()
    }

    override fun onResume() {
        //setWindowSoftInput(float = mDatabind.llInput, setPadding = true)
        mDatabind.smartChat.postDelayed({
            try {
                /*  mDatabind.root.height.toString().loge("========onResume===")
                  mDatabind.notice.root.height.toString().loge("=onResume===")
                  mDatabind.smartChat.height.toString().loge("=onResume===")
                  mDatabind.rcvChat.height.toString().loge("=onResume===")*/
                val params = mDatabind.rcvChat.layoutParams
                params.height = mDatabind.smartChat.height
                mDatabind.rcvChat.layoutParams = params
            } catch (_: Exception) {
            }
        }, 200)
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onPause() {
        hideSoftInput()
        mAgentWeb?.webCreator?.webView?.onPause()
        mDatabind.edtChatMsg.clearFocus()
        /* mDatabind.smartChat.height.toString().loge("=onPause===")
         mDatabind.rcvChat.height.toString().loge("=onPause===")*/
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (!isTopActivity(activity)) {
            // exitRoom()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exitRoom()
    }

    private fun exitRoom() {
        onWsUserExitRoom(liveId)
        MyWsManager.getInstance(App.app)?.removeLiveRoomListener(activity.toString())
        isEnterRoom = false
        mAgentWeb?.webLifeCycle?.onDestroy()
    }

    override fun onEnterRoomInfo(isOk: Boolean, msg: ReceiveWsBean<*>) {
        //  myToast("enter room ==$isOk")
        isEnterRoom = true
    }

    override fun onExitRoomInfo(isOk: Boolean, msg: ReceiveWsBean<*>) {
        // myToast("exit Room ==$isOk")
    }

    override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {
        //myToast("send_msg ==$isOk")
    }

    override fun onRoomReceive(chat: ReceiveChatMsg) {
        var isShowBottom = false
        val firstVisible: Int = mLayoutManager.findFirstVisibleItemPosition()
        val lastVisible: Int = mLayoutManager.findLastVisibleItemPosition()
        if (chat.groupId != liveId) {
            return
        }
        mDatabind.smartChat.showContent()
        mDatabind.rcvChat.models?.apply {
            if (this.size > 3 && lastVisible < 1) {
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
            )
        ) // 添加一条消息

        mDatabind.rcvChat.models?.size?.let {
            // mDatabind.rcvChat.adapter?.notifyItemInserted(it-1)
            // mDatabind.rcvChat.adapter?.notifyItemRangeChanged(it, 1);
            // mLayoutManager.scrollToPositionWithOffset(it, Integer.MIN_VALUE)
            if (chat.from == CacheUtil.getUser()?.id || lastVisible == it - 2) {
                mDatabind.rcvChat.smoothScrollToPosition(it)
            }
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            mDatabind.sendChat -> {
                sendMsg()
            }
        }
    }

    private fun sendMsg() {
        if (mViewModel.input.get().isEmpty()){
            return
        }
        hideSoftInput()
        mDatabind.edtChatMsg.clearFocus()
        mDatabind.sendChat.postDelayed({
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
        }, 400)
    }

    /**
     * 切换直播间
     */
    private fun updateChatRoom(liveId: String, userId: String?) {
        onWsUserExitRoom(this.liveId)
        this.liveId = liveId
        this.userId = userId
        offset = ""
        setNotice()
        mDatabind.rcvChat.postDelayed({
            //布局重新计算
            try {
                val params = mDatabind.rcvChat.layoutParams
                params.height = mDatabind.smartChat.height
                mDatabind.rcvChat.layoutParams = params
            } catch (_: Exception) {
            }
            mDatabind.rcvChat.models = arrayListOf()
            mDatabind.smartChat.showContent()
            onWsUserEnterRoom(liveId)
            getHistoryData()
        }, 200)

    }

}

