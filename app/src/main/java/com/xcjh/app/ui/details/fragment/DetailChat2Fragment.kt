package com.xcjh.app.ui.details.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Layout
import android.text.method.LinkMovementMethod
import android.text.style.AlignmentSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProvider
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
import com.xcjh.base_lib.utils.view.visibleOrGone
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolverDef
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.RenderProps
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlParser
import io.noties.markwon.html.MarkwonHtmlParserImpl
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler
import io.noties.markwon.html.tag.SimpleTagHandler
import kotlinx.android.synthetic.main.fragment_detail_tab_chat.view.*
import org.commonmark.parser.Parser
import java.util.Collections
import java.util.regex.Pattern


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
             //   mAgentNoticeWeb = agentWeb(requireContext(), mDatabind.notice.agentWeb){}
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

    private var mAgentWeb: AgentWeb? = null
    private var mAgentNoticeWeb: AgentWeb? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun initRcv() {
        //  mDatabind.page.setEnableLoadMore(false)
        mDatabind.page.setEnableOverScrollBounce(false)
        mDatabind.page.preloadIndex = 3
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
        setChatRoomRcv(mDatabind.rcvChat, mLayoutManager, true, {
            mAgentWeb = it
        },{
            mDatabind.rcvChat.postDelayed({
                mDatabind.rcvChat.smoothScrollToPosition(0)
            }, 100)

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
/*    private fun createCustomHtmlRenderer(configuration: HtmlConfigure): HtmlConfigure {
        return object : HtmlConfigure(configuration) {
            fun renderStyle(builder: StringBuilder, styles: MutableCollection<String>) {
                super.renderStyle(builder, styles)
                builder.append(" style=\"")
                builder.append(styles.joinToString(";"))
                builder.append("\"")
            }

            override fun configureHtml(plugin: HtmlPlugin) {
                plugin
            }
        }
    }*/
    override fun createObserver() {
        //公告
        vm.anchor.observe(this) {
            if (it != null) {
                noticeBean.notice = it.notice ?: ""

                val bb =
                    "<html><head><style>body { font-size:14px; color: #ffffff; margin: 0; }</style></head><body>${(noticeBean.notice)}</body></html>"
            /*    val markwon =  Markwon.builder(requireContext())
                    .usePlugin(CorePlugin.create())
                    .usePlugin(HtmlPlugin.create { plugin ->
                        plugin.excludeDefaults(true)
                        plugin.configureParser(MarkwonHtmlParserImpl.create())
                    })
                    .build()*/
                val markwon =  Markwon.builder(requireContext())
                    .usePlugin(HtmlPlugin.create())
                    .build()
                // 定义要匹配的正则表达式模式
                val pattern = Pattern.compile("rgb\\([^)]+\\)")

                // 创建 Matcher 对象并进行匹配操作
                val matcher = pattern.matcher(noticeBean.notice)
                var replace=noticeBean.notice
              /*  while (matcher.find()) {
                    val list = convertToList(matcher.group())
                    val hexColor = convertToHexColor(list[0], list[1], list[2])
                    matcher.group().loge("99999===")
                    hexColor.loge("99999===")
                    replace = replace.replace(matcher.group(), hexColor)
                    noticeBean.notice.loge("99999===")
                    replace.loge("99999===")
                }*/
                val toMarkdown = markwon.toMarkdown(replace)
                markwon.setParsedMarkdown(mDatabind.notice.expandableText, toMarkdown)
               // mDatabind.notice.expandableText.text=toMarkdown
               // mDatabind.notice.expandableText.text = it.notice?.toHtml() //主播公告
                val lineCount: Int = mDatabind.notice.expandableText.layout.lineCount
                mDatabind.notice.lltExpandCollapse.visibleOrGone(lineCount > 2)
                if (lineCount > 2) {
                    // 内容超过了两行
                    mDatabind.notice.expandableText.maxLines = 2
                }
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
                     /*   mDatabind.rcvChat.postDelayed({

                        }, 500)*/

                    } catch (_: Exception) {
                    }

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
        mDatabind.page.postDelayed({
            try {
                val params = mDatabind.rcvChat.layoutParams
                params.height = mDatabind.page.height
                mDatabind.rcvChat.layoutParams = params
            } catch (_: Exception) {
            }
        }, 200)
        mAgentNoticeWeb?.webLifeCycle?.onResume()
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onPause() {
        hideSoftInput()
        mDatabind.edtChatMsg.clearFocus()
        mAgentNoticeWeb?.webLifeCycle?.onPause()
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (!isTopActivity(activity)) {
            // exitRoom()
        }
        //hideSoftInput()
    }

    override fun onDestroy() {
        mAgentNoticeWeb?.webLifeCycle?.onDestroy()
        mAgentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
        exitRoom()
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

