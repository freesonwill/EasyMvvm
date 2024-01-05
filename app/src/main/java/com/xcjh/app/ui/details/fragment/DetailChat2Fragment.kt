package com.xcjh.app.ui.details.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.layoutmanager.HoverLinearLayoutManager
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.softinput.hideSoftInput
import com.google.gson.Gson
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebViewClient
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
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
    }

    private fun setNotice() {
        if (!userId.isNullOrEmpty()) {
            mDatabind.notice.root.visibility = View.VISIBLE
            mDatabind.notice.apply {
                mNoticeWeb = agentWeb
                setWeb(mNoticeWeb!!){}
                lltExpandCollapse.setOnClickListener {
                    noticeBean.isOpen = !noticeBean.isOpen
                    tvArrow.text =
                        if (noticeBean.isOpen) getString(R.string.pack_up) else getString(R.string.expand)
                    startImageRotate(expandCollapse, noticeBean.isOpen)
                    var htmlText = if (noticeBean.isOpen){
                        "<div style='display: -webkit-box; -webkit-line-clamp: 10; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${noticeBean.notice}</div>"
                    }else{
                        "<div style='display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${noticeBean.notice}</div>"
                    }
                    val bb = "<html><head><style>body { font-size:14px; color: #94999f; margin: 0; }</style></head><body>${(htmlText)}</body></html>"
                    agentWeb.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)
                    mDatabind.rcvChat.postDelayed({
                        try {
                            val params = mDatabind.rcvChat.layoutParams
                            params.height = mDatabind.page.height
                            mDatabind.rcvChat.layoutParams = params
                        } catch (_: Exception) {
                        }
                    }, 100)

                }
            }
        } else {
            mDatabind.notice.root.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initRcv() {
        //  mDatabind.page.setEnableLoadMore(false)
        mDatabind.page.setEnableOverScrollBounce(false)
        mDatabind.page.preloadIndex = 6
        ClassicsFooter.REFRESH_FOOTER_NOTHING=""
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
            mViewModel.getHisMsgList(liveId, offset)
        }
        setChatRoomRcv(vm,mDatabind.rcvChat, mLayoutManager, true, {
            mAgentWeb = it
        },{
            mDatabind.rcvChat.postDelayed({
                mDatabind.rcvChat.smoothScrollToPosition(0)
            }, 200)

        }) {
            offset = it ?: ""
        }
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
               mDatabind.edtChatMsg.isEnabled = true
           }catch (e:Exception){
             // e.message?.loge()
           }
        }, 1000)
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
        mViewModel.getHisMsgList(liveId, offset, true)
    }

    override fun createObserver() {
        //公告
        vm.anchor.observe(this) {
            if (it != null) {
                noticeBean.notice = it.notice ?: ""
                noticeBean.isOpen = false
                mDatabind.notice.expandableText.text = noticeBean.notice.replace("<p>","<span>").replace("</p>","</span>").toHtml()
                val layout = mDatabind.notice.expandableText.layout
                if (layout != null) {
                    val lineCount: Int = layout.lineCount
                    lineCount.toString().loge("888===")
                    mDatabind.notice.lltExpandCollapse.visibleOrGone(lineCount > 2)
                    //mDatabind.notice.expandableText.maxLines =  2
                }
                val htmlText =
                    "<div style='display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;'>${noticeBean.notice}</div>"
                val bb = "<html><head><style>body { font-size:14px; color: #94999f; margin: 0; }</style></head><body>${(htmlText)}</body></html>"
                mNoticeWeb?.loadDataWithBaseURL(null, bb, "text/html", "UTF-8", null)

                mDatabind.rcvChat.postDelayed({
                    try {
                        val params = mDatabind.rcvChat.layoutParams
                        params.height = mDatabind.page.height
                        mDatabind.rcvChat.layoutParams = params
                        mDatabind.rcvChat.addModels(
                            listOf(
                                //BottomMsgBean(),
                                FirstMsgBean(
                                    it.id, it.head, it.nickName, "0",
                                    //richText,
                                    it.firstMessage ?: "", identityType = 1
                                )
                            ),
                            index = 0
                        ) // 添加一条消息 
                        mDatabind.rcvChat.scrollToPosition(0)
                    } catch (_: Exception) { }
                }, 500)
            }
        }
        //历史消息
        mViewModel.hisMsgList.observe(this) {
            // mDatabind.page.finishRefresh()
            if (it.isSuccess) {
                if (it.listData.isEmpty()) {
                    //mDatabind.smartChat.setEnableRefresh(false)
                    if (it.isRefresh && userId.isNullOrEmpty()) {
                        mDatabind.page.showEmpty()
                    } else {
                        mDatabind.page.showContent(false)
                    }
                } else {
                    mDatabind.page.showContent(it.listData.size == req.size)
                    mDatabind.page.finish(true, it.listData.size == req.size)
                    mDatabind.rcvChat.addModels(it.listData.reversed()) // 添加一条消息
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
        MyWsManager.getInstance(App.app)?.setLiveRoomListener(activity.toString(), this)
        onWsUserEnterRoom(liveId)
    }

    override fun onResume() {
        super.onResume()
        mDatabind.page.postDelayed({
            try {
                val params = mDatabind.rcvChat.layoutParams
                params.height = mDatabind.page.height
                mDatabind.rcvChat.layoutParams = params
            } catch (_: Exception) {
            }
        }, 200)
        mNoticeWeb?.onResume()
        mAgentWeb?.webLifeCycle?.onResume()
    }

    override fun onPause() {
        super.onPause()
        hideSoftInput()
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

    private fun sendMsg() {
        if (mViewModel.input.get().isEmpty()) {
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
        mDatabind.page.resetNoMoreData()
        mDatabind.rcvChat.postDelayed({
            //布局重新计算
            try {
                val params = mDatabind.rcvChat.layoutParams
                params.height = mDatabind.page.height
                mDatabind.rcvChat.layoutParams = params
            } catch (_: Exception) {
            }
            mDatabind.rcvChat.models = arrayListOf()
            mDatabind.page.showContent()
            onWsUserEnterRoom(liveId)
            getHistoryData()
        }, 200)

    }
}

