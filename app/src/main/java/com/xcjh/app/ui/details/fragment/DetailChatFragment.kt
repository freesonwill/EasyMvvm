package com.xcjh.app.ui.details.fragment

import android.annotation.SuppressLint
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils.dp2px
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drake.brv.animation.SlideLeftItemAnimation
import com.drake.brv.layoutmanager.HoverLinearLayoutManager
import com.drake.brv.utils.addModels
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.drake.softinput.hideSoftInput
import com.google.gson.Gson
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseVpFragment
import com.xcjh.app.bean.*
import com.xcjh.app.databinding.*
import com.xcjh.app.isTopActivity
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.common.MyItemAnimator
import com.xcjh.app.ui.details.common.RoomChatVm
import com.xcjh.app.ui.details.common.SlideInLeftAnimator
import com.xcjh.app.utils.*
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.bean.SendChatMsgBean
import com.xcjh.app.websocket.listener.LiveRoomListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.SpanUtil
import com.xcjh.base_lib.utils.view.visibleOrGone
import kotlinx.android.synthetic.main.fragment_detail_tab_chat.view.*


/**
 * 聊天 正向布局
 */

class DetailChatFragment(var liveId: String, var userId: String?, override val typeId: Long = 1) :
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
            itemHover = true)
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
                expandableText.text = noticeBean.notice
                expandCollapse.setOnClickListener {
                    val aa = expandableText.height
                    noticeBean.isOpen = !noticeBean.isOpen
                    startImageRotate(expandCollapse, noticeBean.isOpen)
                    expandableText.maxLines = if (noticeBean.isOpen) 10 else 2
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initRcv() {
        mDatabind.smartChat.setEnableLoadMore(false)
        mDatabind.smartChat.setOnRefreshListener {
            mViewModel.getHisMsgList(liveId, offset)
        }
        mDatabind.rcvChat.apply {
            layoutManager = mLayoutManager
        }.setup {
            addType<NoticeBean> {
                R.layout.item_detail_chat_notice // 公告
            }
            addType<MsgBean> {
                R.layout.item_detail_chat // 我发的消息
            }
            onBind {
                when (val item = _data) {
                    is NoticeBean -> {
                        val binding = getBinding<ItemDetailChatNoticeBinding>()
                        binding.expandableText.text = item.notice
                        binding.expandCollapse.setOnClickListener {
                            item.isOpen = !item.isOpen
                            startImageRotate(binding.expandCollapse, item.isOpen)
                            getBinding<ItemDetailChatNoticeBinding>().expandableText.maxLines =
                                if (item.isOpen) 10 else 2
                            // mDatabind.rcvChat.scrollToPosition(modelPosition)
                        }

                    }
                    is MsgBean -> {
                        val binding = getBinding<ItemDetailChatBinding>()
                        if (item.identityType == 0) {
                            binding.ivImage.visibleOrGone(false)
                            binding.tvType.text = getLeverNum(item.level)
                            binding.tvType.paint.shader =
                                LinearGradient(0f, 0f, 0f, binding.tvType.lineHeight.toFloat(),
                                    if (item.level == "7") appContext.getColor(R.color.c_v7) else setLeverColor(
                                        item.level),
                                    if (item.level == "7") appContext.getColor(R.color.c_v77) else setLeverColor(
                                        item.level),
                                    Shader.TileMode.CLAMP)
                            binding.tvType.setBackgroundResource(setLeverDrawable(item.level))
                            // binding.sltLevel.setStrokeColor(setLeverColor(item.level))
                            //binding.tvType.setTextColor(setLeverColor(item.level))
                        } else {
                            binding.tvType.text = getString(R.string.anchor)
                            binding.tvType.setBackgroundResource(setLeverDrawable("2"))
                            binding.tvType.setTextColor(setLeverColor("2"))
                            binding.ivImage.visibleOrGone(item.msgType == 1)
                            if (item.msgType == 1) {//图片
                                Glide.with(context)
                                    .load(item.content)
                                    .apply(RequestOptions().transform(CenterCrop(),
                                        RoundedCorners(dp2px(8f))))
                                    .placeholder(R.drawable.load_square)
                                    .into(binding.ivImage)
                                binding.ivImage.setOnClickListener {
                                    val list = arrayListOf<LocalMedia>()
                                    val localMedia = LocalMedia()
                                    localMedia.path = item.content
                                    localMedia.cutPath = item.content
                                    list.add(localMedia)
                                    PictureSelector.create(context)
                                        .openPreview()
                                        .setImageEngine(GlideEngine.createGlideEngine())
                                        .startActivityPreview(0, false, list)
                                }
                            }
                        }
                        if (modelPosition == 0) {
                            offset = item.id ?: ""
                        }
                        SpanUtil.create()
                            .addForeColorSection(item.nick + " : ",
                                ContextCompat.getColor(context, R.color.c_8a91a0))
                            .addForeColorSection(if (item.msgType == 1) "" else item.content,
                                ContextCompat.getColor(context, R.color.c_F5F5F5))
                            .showIn(binding.tvContent) //显示到控件TextView中
                    }
                }
            }
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
            false
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
                mDatabind.notice.expandableText.text = it.notice //主播公告
                mDatabind.rcvChat.postDelayed({
                    mDatabind.rcvChat.addModels(
                        listOf(MsgBean(it.id,
                            it.head,
                            it.nickName,
                            "0",
                            it.firstMessage ?: "",
                            identityType = 1)),
                        // index = 0
                    ) // 添加一条消息
                    // mDatabind.rcvChat.scrollToPosition(0)
                    mDatabind.rcvChat.models?.size?.let {
                        mDatabind.rcvChat.smoothScrollToPosition(it)
                    }
                }, 500)
            }
        }
        //历史消息
        mViewModel.hisMsgList.observe(this) {

            if (it.isSuccess) {
                if (it.listData.isEmpty()) {
                    mDatabind.smartChat.setEnableRefresh(false)
                    mDatabind.smartChat.finishRefreshWithNoMoreData()
                } else {
                    mDatabind.smartChat.finishRefresh()
                    mDatabind.rcvChat.addModels(it.listData, index = 0) // 添加一条消息
                    if (it.isRefresh) {
                        mDatabind.rcvChat.models?.size?.let {
                            mDatabind.rcvChat.smoothScrollToPosition(it)
                        }
                    }
                }
            } else {
                mDatabind.smartChat.finishRefresh()
                //  mDatabind.rcvChat.addModels(messageList.apply{reverse()})
            }
        }
        vm.anchorInfo.observe(this) {
            updateChatRoom(it.liveId, it.userId)
        }
        appViewModel.wsStatus.observe(this) {
            Log.e("TAG", "createObserver: ====" + it)
            if (isAdded && it == 1) {
                //断开后重连成功，重新进入房间
                MyWsManager.getInstance(App.app)?.setLiveRoomListener(activity.toString(), this)
                onWsUserEnterRoom(liveId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isEnterRoom) {
            MyWsManager.getInstance(App.app)?.setLiveRoomListener(activity.toString(), this)
            onWsUserEnterRoom(liveId)
        }
        // Log.e("===", "onStart:isTopActivity ===" + activity.toString())
    }

    override fun onResume() {
        //setWindowSoftInput(float = mDatabind.llInput, setPadding = true)
        mDatabind.rcvChat.postDelayed({
            //Log.e("===", "onResume:isTopActivity ===" + this.mDatabind.root.height + "=====" + mDatabind.rcvChat.height)
          /*  val params = mDatabind.rcvChat.layoutParams
            params.height = mDatabind.rcvChat.height//+10
            mDatabind.rcvChat.layoutParams = params*/
        }, 800)
        super.onResume()
        //Log.e("===", "onResume:isTopActivity ==="+activity.toString())
    }

    override fun onPause() {
        hideSoftInput()
        mDatabind.rcvChat.postDelayed({
            Log.e("===",
                "onPause:isTopActivity ===" + this.mDatabind.root.height + "=====" + mDatabind.rcvChat.height)
        }, 200)
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (!isTopActivity(activity)) {
            exitRoom()
        }
        //Log.e("===", "onStop:isTopActivity ===${KtxActivityManger.currentActivity.toString()}====="+isTopActivity(activity))
    }

    override fun onDestroy() {
        super.onDestroy()
        exitRoom()
        // Log.e("===", "onDestroy:isTopActivity ==="+activity.toString())
    }

    private fun exitRoom() {
        onWsUserExitRoom(liveId)
        MyWsManager.getInstance(App.app)?.removeLiveRoomListener(activity.toString())
        isEnterRoom = false
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
        Log.e("TAG", "find: ===" + firstVisible + "=======" + lastVisible)
        mDatabind.rcvChat.models?.apply {
            if (this.size > 3 && lastVisible < 1) {
                isShowBottom = true
            }
        }

        mDatabind.rcvChat.addModels(listOf(MsgBean(
            chat.from,
            chat.fromAvatar,
            chat.fromNickName ?: "",
            chat.level,
            chat.content,
            msgType = chat.msgType,
            identityType = chat.identityType,
        ))) // 添加一条消息

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
                hideSoftInput()
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
                                    // Log.e("TAG", "onClick: ====" + Gson().toJson(this))
                                })
                            )

                        }
                        mViewModel.input.set("")
                    }
                }, 400)
            }
        }
    }

    /**
     * 切换直播间
     */
    private fun updateChatRoom(liveId: String, userId: String?) {
        Log.e("TAG", "updateChatRoom: ====")
        onWsUserExitRoom(this.liveId)
        this.liveId = liveId
        this.userId = userId
        offset = ""
        setNotice()
        mDatabind.rcvChat.postDelayed({
            //聊天数据重置
            mDatabind.rcvChat.models = arrayListOf()
            onWsUserEnterRoom(liveId)
            getHistoryData()
        }, 200)

    }

}

