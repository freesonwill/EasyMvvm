package com.xcjh.app.ui.chat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.drake.statelayout.StateConfig
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.InjectResourceSource
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.manager.PictureCacheManager
import com.scwang.smart.refresh.header.ClassicsHeader
import com.xcjh.app.MyApplication
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityChatBinding
import com.xcjh.app.databinding.ItemChatPicLeftBinding
import com.xcjh.app.databinding.ItemChatPicRightBinding
import com.xcjh.app.databinding.ItemChatTxtLeftBinding
import com.xcjh.app.databinding.ItemChatTxtRightBinding
import com.xcjh.app.net.CountingRequestBody
import com.xcjh.app.net.ProgressListener
import com.xcjh.app.bean.MsgBeanData
import com.xcjh.app.bean.NewsBean
import com.xcjh.app.databinding.ItemChatVideoLeftBinding
import com.xcjh.app.databinding.ItemChatVideoRightBinding
import com.xcjh.app.databinding.ItemEmojeTxtBinding
import com.xcjh.app.databinding.ItemNewsListBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.utils.CacheDataList
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.ChatTimeUtile
import com.xcjh.app.utils.GlideEngine
import com.xcjh.app.utils.loadImageWithGlide
import com.xcjh.app.utils.nice.Utils
import com.xcjh.app.utils.picture.ImageFileCompressEngine
import com.xcjh.app.utils.reSendMsgDialog
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.FeedSystemNoticeBean
import com.xcjh.app.websocket.bean.ReadWsBean
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.bean.SendChatMsgBean
import com.xcjh.app.websocket.bean.SendCommonWsBean
import com.xcjh.app.websocket.listener.C2CListener
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TAG
import com.xcjh.base_lib.utils.TimeUtil
import com.xcjh.base_lib.utils.copyToClipboard
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.grid
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import okhttp3.MultipartBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


/***
 * 私聊页面
 * 必传参数
 * Constants.USER_ID 主播ID
 * Constants.USER_NICK  主播昵称
 * Constants.USER_HEAD   主播头像
 */
class ChatActivity : BaseActivity<ChatVm, ActivityChatBinding>() {

    var options: RequestOptions? = null
    var userId = ""
    var nickname = ""

    var userhead = ""
    var isShowBottom = false  //是否打开了更多
    var isShowEmoje=false//是否打开了emoje
    var lastShowTimeStamp: Long = 0
    var timecurrent: String = ""
    val baseLong: Long = 0
    var listdata: MutableList<MsgBeanData> = ArrayList<MsgBeanData>()
    var msgType = 0//消息类型，文字：0， 图片：1
    var msgContent = ""
    var isUpdata = false
    private val delayTime: Long = 10000
    private val listPic = java.util.ArrayList<LocalMedia>()
    private val mutex = Mutex()
    var incount = 0


    override fun initView(savedInstanceState: Bundle?) {
//        mViewModel.getAllKeysFromJson()

        //收到通知其他地方登录
        appViewModel.quitLoginEvent.observe(this){
            finish()
        }
        try {


            options = RequestOptions()
                .transform(CenterCrop(), RoundedCorners(Utils.dp2px(this, 8f)))
            ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .keyboardEnable(true)
                .navigationBarColor(R.color.c_ffffff)
                .navigationBarDarkIcon(true)
                .titleBar(mDatabind.titleTop.root)
                .init()
            MyWsManager.getInstance(this)?.sendMessage(
                Gson().toJson(
                    SendCommonWsBean(
                        5,
                        CacheUtil.getUser()?.id,
                        CacheUtil.getToken()
                    )
                )
            )
            val rootView = findViewById<View>(android.R.id.content)
            // 监听视图树变化
            rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val rect = Rect()
                    rootView.getWindowVisibleDisplayFrame(rect)

                    // 计算可见屏幕高度和整个屏幕高度的差值
                    val screenHeight = rootView.height
                    val keypadHeight = screenHeight - rect.bottom

                    // 判断软键盘是否显示
                    val isKeyboardShowing = keypadHeight > screenHeight * 0.15

                    if (isKeyboardShowing) {
                        // 软键盘弹出回调
                        // 在这里执行相应操作
//                        if(isShowBottom){
//                          mDatabind.linbottom.setVisibility(View.GONE)
//                        }

                    } else {
                        // 软键盘隐藏回调
                        // 在这里执行相应操作
                        if(isShowBottom){
//                            mDatabind.linbottom.setVisibility(View.VISIBLE)
                        }
                    }
                }
            })

//            ClassicsHeader
            mDatabind.smartCommon.setRefreshHeader(ClassicsHeader(this))
            mDatabind.state.apply {

                StateConfig.setRetryIds(R.id.ivEmptyIcon, R.id.txtEmptyName)
                onEmpty {
                    this.findViewById<TextView>(R.id.txtEmptyName).text =
                        resources.getString(R.string.nomsgrecoder)
                    this.findViewById<ImageView>(R.id.ivEmptyIcon)
                        .setImageDrawable(resources.getDrawable(R.drawable.ic_empety_msg))
                    this.findViewById<ImageView>(R.id.ivEmptyIcon).setOnClickListener { }
                }
                onLoading {
                    LogUtils.d("")
                }
                onRefresh {
                    LogUtils.d("")
                }
            }
            // 键盘弹出平滑动画
            userId = intent.getStringExtra(Constants.USER_ID) ?: ""
            nickname = intent.getStringExtra(Constants.USER_NICK) ?: ""
            userhead = intent.getStringExtra(Constants.USER_HEAD) ?: ""

            mDatabind.smartCommon.setOnClickListener {
                hideSoftKeyBoard(this)
            }

            appViewModel.updateMsgEvent.postValue(userId)
            mDatabind.titleTop.tvTitle.text = nickname
//        Glide.with(this).load(userhead).placeholder(R.drawable.default_anchor_icon)
//            .into(mDatabind.titleTop.ivhead)
//        (mDatabind.rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
//            false//防止item刷新的时候闪烁
            mDatabind.rv.setHasFixedSize(true)
            mDatabind.rv.setup {
                addType<MsgBeanData> {
                    when (fromId) {
                        CacheUtil.getUser()?.id, "", null -> {
                            when (msgType) {
                                0 -> {
                                    R.layout.item_chat_txt_right
                                }

                                1 -> {
                                    R.layout.item_chat_pic_right
                                }
                                3 -> {
                                    R.layout.item_chat_video_right
                                }

                                else -> {
                                    R.layout.item_chat_txt_right
                                }
                            }

                        }

                        else -> {
                            when (msgType) {
                                0 -> {
                                    R.layout.item_chat_txt_left
                                }

                                1 -> {
                                    R.layout.item_chat_pic_left
                                }
                                3-> {
                                    R.layout.item_chat_video_left
                                }

                                else -> {
                                    R.layout.item_chat_txt_left
                                }
                            }
                        }
                    }
                }
                onBind {
                    when (itemViewType) {
                        //左边文字消息
                        R.layout.item_chat_txt_left -> {
                            var binding = getBinding<ItemChatTxtLeftBinding>()
                            var ad = _data as MsgBeanData
                            if(0==layoutPosition){
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,context.dp2px(20))
                                binding.linroot.layoutParams = layoutParams
                            }else{
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,0)
                                binding.linroot.layoutParams = layoutParams
                            }
                            binding.tvcontent.text = ad.content
                            timecurrent = ChatTimeUtile.formatTimestamp(
                                context,
                                ad.createTime!!.toLong()
                            )!!
                            binding.tvtime.text = timecurrent
                            if (bindingAdapterPosition < (adapter.models!!.size - 1)) {
                                var adnext =
                                    adapter.models!![bindingAdapterPosition + 1] as MsgBeanData
                                var nexttime = ChatTimeUtile.formatTimestamp(
                                    context,
                                    adnext.createTime!!.toLong()
                                )
                                if (timecurrent == nexttime) {
                                    binding.tvtime.visibility = View.GONE
                                } else {
                                    if (TimeUtil.isTimeDifferenceGreaterThan15Minutes(
                                            ad.createTime!!, adnext.createTime!!

                                        )
                                    ) {
                                        if (ad.lastShowTimeStamp!! == baseLong) {
                                            ad.lastShowTimeStamp = -1
                                        }
                                        lastShowTimeStamp = ad.createTime!!
                                        binding.tvtime.visibility = View.VISIBLE
                                    } else {
                                        binding.tvtime.visibility = View.GONE
                                    }
                                }
                            } else {
                                binding.tvtime.visibility = View.VISIBLE
                            }
                            if (ad.lastShowTimeStamp!! == baseLong) {
                                ad.lastShowTimeStamp = lastShowTimeStamp
                            }
                            // binding.tvtime.visibility = View.GONE
                            Glide.with(this@ChatActivity).load(userhead)
                                .placeholder(R.drawable.default_anchor_icon).into(binding.ivhead)


                            binding.tvcontent.setOnLongClickListener(OnLongClickListener {
                                initLongClick(binding.tvcontent, ad.content, bindingAdapterPosition)
                                true
                            })
                            binding.linroot.setOnClickListener {
                                hideSoftKeyBoard(this@ChatActivity)
                            }

                        }
                        //右边文字
                        R.layout.item_chat_txt_right -> {//正在进行中的比赛
                            var binding = getBinding<ItemChatTxtRightBinding>()
                            var matchBeanNew = _data as MsgBeanData
                            if(0==layoutPosition){
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,context.dp2px(20))
                                binding.linroot.layoutParams = layoutParams
                            }else{
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,0)
                                binding.linroot.layoutParams = layoutParams
                            }
                            when (matchBeanNew.sentNew) {
                                0 -> {//正在发送
                                    addDataToList("1", matchBeanNew)
                                    binding.googleProgress.visibility = View.VISIBLE
                                    binding.ivfaile.visibility = View.GONE
                                    GlobalScope.launch {
                                        delay(delayTime)
                                        if (matchBeanNew.sentNew == 0) {//发送失败
                                            matchBeanNew.sentNew = 2
                                            addDataToList("2", matchBeanNew)
//                                            if (bindingAdapterPosition == 0) {
//                                                appViewModel.updateMsgListEvent.postValue(
//                                                    matchBeanNew
//                                                )
//                                            }
                                            runOnUiThread {
                                                binding.googleProgress.visibility = View.GONE
                                                binding.ivfaile.visibility = View.VISIBLE
                                            }

                                        } else {//发送成功
                                            matchBeanNew.sentNew = 1

                                        }

                                    }
                                }

                                1, 3 -> {//发送成功

                                    matchBeanNew.sentNew = 1
                                    //  addDataToList("3", matchBeanNew)
                                    binding.googleProgress.visibility = View.GONE
                                    binding.ivfaile.visibility = View.GONE
                                }

                                2 -> {//发送失败
                                    binding.googleProgress.visibility = View.GONE
                                    binding.ivfaile.visibility = View.VISIBLE
                                }

                            }

                            binding.ivfaile.setOnClickListener {

                                reSendMsg(
                                    matchBeanNew,
                                    bindingAdapterPosition
                                )

                            }

                            binding.tvcontent.text =
                                matchBeanNew.content
                            Glide.with(this@ChatActivity).load(CacheUtil.getUser()?.head)
                                .placeholder(R.drawable.icon_login_my_head)
                                .into(binding.ivhead)
                            if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                matchBeanNew.lastShowTimeStamp = lastShowTimeStamp
                            }
                            timecurrent = ChatTimeUtile.formatTimestamp(
                                context,
                                matchBeanNew.createTime!!.toLong()
                            )!!
                            binding.tvtime.text = timecurrent
                            if (bindingAdapterPosition < (adapter.models!!.size - 1)) {
                                var adnext =
                                    adapter.models!![bindingAdapterPosition + 1] as MsgBeanData
                                var nexttime = ChatTimeUtile.formatTimestamp(
                                    context,
                                    adnext.createTime!!.toLong()
                                )
                                if (timecurrent == nexttime) {
                                    binding.tvtime.visibility = View.GONE
                                } else {
                                    if (TimeUtil.isTimeDifferenceGreaterThan15Minutes(
                                            matchBeanNew.createTime!!, adnext.createTime!!

                                        )
                                    ) {
                                        if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                            matchBeanNew.lastShowTimeStamp = -1
                                        }
                                        lastShowTimeStamp = matchBeanNew.createTime!!
                                        binding.tvtime.visibility = View.VISIBLE
                                    } else {
                                        binding.tvtime.visibility = View.GONE
                                    }
                                }
                            } else {
                                binding.tvtime.visibility = View.VISIBLE
                            }
                            binding.tvcontent.setOnLongClickListener(OnLongClickListener {
                                initLongClick(
                                    binding.tvcontent,
                                    matchBeanNew.content,
                                    bindingAdapterPosition
                                )
                                true
                            })
                            binding.linroot.setOnClickListener {
                                hideSoftKeyBoard(this@ChatActivity)
                            }

                        }
                        //右边图片
                        R.layout.item_chat_pic_right -> {//右边图片
                            var binding = getBinding<ItemChatPicRightBinding>()
                            var matchBeanNew = _data as MsgBeanData
                            if(0==layoutPosition){
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,context.dp2px(20))
                                binding.linroot.layoutParams = layoutParams
                            }else{
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,0)
                                binding.linroot.layoutParams = layoutParams
                            }
                            when (matchBeanNew.sentNew) {
                                0 -> {//正在发送
                                    LogUtils.d(
                                        bindingAdapterPosition.toString() + "准备发送" +
                                                JSONObject.toJSONString(_data as MsgBeanData)
                                    )
                                    addDataToList("4", (_data as MsgBeanData))
                                    binding.googleProgress.visibility = View.VISIBLE
                                    binding.ivfaile.visibility = View.GONE
                                    GlobalScope.launch {

                                        upLoadPic(
                                            bindingAdapterPosition,
                                            matchBeanNew,
                                            binding.tvpross,
                                            binding.ivfaile
                                        )
                                         delay(delayTime)

                                        if (matchBeanNew.sentNew == 0) {//发送失败
                                            matchBeanNew.sentNew = 2

                                            addDataToList("5", matchBeanNew)
//                                            if (bindingAdapterPosition == 0) {
//                                                appViewModel.updateMsgListEvent.postValue(
//                                                    matchBeanNew
//                                                )
//                                            }

                                            runOnUiThread {
                                                binding.googleProgress.visibility = View.GONE
                                                binding.ivfaile.visibility = View.VISIBLE



                                            }

                                        } else {//发送成功
                                            (_data as MsgBeanData).sentNew = 1

                                        }

                                    }
                                }

                                1, 3 -> {//发送成功
                                    (_data as MsgBeanData).sentNew = 1
                                    //  addDataToList("6", (_data as MsgBeanData))
                                    binding.googleProgress.visibility = View.GONE
                                    binding.ivfaile.visibility = View.GONE
                                    LogUtils.d(
                                        bindingAdapterPosition.toString() + "99发送chenggong" +
                                                JSONObject.toJSONString(_data as MsgBeanData)
                                    )
                                }

                                2 -> {//发送失败
                                    binding.googleProgress.visibility = View.GONE
                                    binding.ivfaile.visibility = View.VISIBLE
                                }

                                else -> {
                                    binding.googleProgress.visibility = View.GONE
                                    binding.ivfaile.visibility = View.GONE
                                }

                            }
                            binding.ivfaile.setOnClickListener {
                                reSendMsg(
                                    matchBeanNew,
                                    bindingAdapterPosition
                                )

                            }
                            timecurrent = ChatTimeUtile.formatTimestamp(
                                context,
                                matchBeanNew.createTime!!.toLong()
                            )!!
                            binding.tvtime.text = timecurrent
                            if (bindingAdapterPosition < (adapter.models!!.size - 1)) {
                                var adnext =
                                    adapter.models!![bindingAdapterPosition + 1] as MsgBeanData
                                var nexttime = ChatTimeUtile.formatTimestamp(
                                    context,
                                    adnext.createTime!!.toLong()
                                )
                                if (timecurrent == nexttime) {
                                    binding.tvtime.visibility = View.GONE
                                } else {
                                    if (TimeUtil.isTimeDifferenceGreaterThan15Minutes(
                                            matchBeanNew.createTime!!, adnext.createTime!!

                                        )
                                    ) {
                                        if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                            matchBeanNew.lastShowTimeStamp = -1
                                        }
                                        lastShowTimeStamp = matchBeanNew.createTime!!
                                        binding.tvtime.visibility = View.VISIBLE
                                    } else {
                                        binding.tvtime.visibility = View.GONE
                                    }
                                }
                            } else {
                                binding.tvtime.visibility = View.VISIBLE
                            }
                            if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                matchBeanNew.lastShowTimeStamp = lastShowTimeStamp
                            }



                            if (binding.ivhead.tag == null) {
                                binding.ivhead.tag = CacheUtil.getUser()?.head
                                Glide.with(this@ChatActivity).load(CacheUtil.getUser()?.head)
                                    .placeholder(R.drawable.icon_login_my_head)
                                    .into(binding.ivhead)
                            }
                            if (binding.ivpic.tag == null || binding.ivpic.tag != matchBeanNew.content) {
                                binding.ivpic.tag = matchBeanNew.content
//                                Glide.with(context)
//                                    .asBitmap()
//                                    .load(R.drawable.chat_icon_placeholder)
//                                    .placeholder(R.drawable.chat_icon_placeholder)
//                                    .error(R.drawable.chat_icon_placeholder)
//                                    .thumbnail(0.1f)
//                                    .skipMemoryCache(false)
                                binding.ivpic.loadImageWithGlide(context, matchBeanNew.content){

                                }
                            }


                            binding.ivpic.setOnClickListener {
                                listPic.clear()
                                var localMedia: LocalMedia = LocalMedia()
                                localMedia?.path = matchBeanNew.content
                                localMedia?.cutPath = matchBeanNew.content
                                listPic.add(localMedia)
                                PictureSelector.create(this@ChatActivity)
                                    .openPreview()
                                    .setImageEngine(GlideEngine.createGlideEngine())
                                    .isPreviewFullScreenMode(false)
                                    .setInjectLayoutResourceListener { context, resourceSource ->
                                        return@setInjectLayoutResourceListener if (resourceSource == InjectResourceSource.PREVIEW_LAYOUT_RESOURCE)
                                            R.layout.ps_custom_fragment_preview else InjectResourceSource.DEFAULT_LAYOUT_RESOURCE
                                    }
                                    .startActivityPreview(0, false, listPic)
                            }

                            binding.linroot.setOnClickListener {
                                hideSoftKeyBoard(this@ChatActivity)
                            }

                        }
                        //左边图片
                        R.layout.item_chat_pic_left -> {//右边图片
                            var binding = getBinding<ItemChatPicLeftBinding>()
                            var matchBeanNew = _data as MsgBeanData
                            if(0==layoutPosition){
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,context.dp2px(20))
                                binding.linroot.layoutParams = layoutParams
                            }else{
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,0)
                                binding.linroot.layoutParams = layoutParams
                            }

                            timecurrent = ChatTimeUtile.formatTimestamp(
                                context,
                                matchBeanNew.createTime!!.toLong()
                            )!!
                            binding.tvtime.text = timecurrent
                            if (bindingAdapterPosition < (adapter.models!!.size - 1)) {
                                var adnext =
                                    adapter.models!![bindingAdapterPosition + 1] as MsgBeanData
                                var nexttime = ChatTimeUtile.formatTimestamp(
                                    context,
                                    adnext.createTime!!.toLong()
                                )
                                if (timecurrent == nexttime) {
                                    binding.tvtime.visibility = View.GONE
                                } else {
                                    if (TimeUtil.isTimeDifferenceGreaterThan15Minutes(
                                            matchBeanNew.createTime!!, adnext.createTime!!

                                        )
                                    ) {
                                        if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                            matchBeanNew.lastShowTimeStamp = -1
                                        }
                                        lastShowTimeStamp = matchBeanNew.createTime!!
                                        binding.tvtime.visibility = View.VISIBLE
                                    } else {
                                        binding.tvtime.visibility = View.GONE
                                    }
                                }
                            } else {
                                binding.tvtime.visibility = View.VISIBLE
                            }
                            if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                matchBeanNew.lastShowTimeStamp = lastShowTimeStamp
                            }
                            binding.ivpic.loadImageWithGlide(context, matchBeanNew.content)


                            Glide.with(this@ChatActivity).load(userhead)
                                .placeholder(R.drawable.default_anchor_icon).into(binding.ivhead)
                            binding.ivpic.setOnClickListener {
                                listPic.clear()
                                var localMedia: LocalMedia = LocalMedia()
                                localMedia?.path = matchBeanNew.content
                                localMedia?.cutPath = matchBeanNew.content
                                listPic.add(localMedia)

                                PictureSelector.create(this@ChatActivity)
                                    .openPreview()
                                    .isPreviewFullScreenMode(false)
                                    .setImageEngine(GlideEngine.createGlideEngine())
                                    .setInjectLayoutResourceListener { context, resourceSource ->//预览无标题栏
                                        return@setInjectLayoutResourceListener if (resourceSource == InjectResourceSource.PREVIEW_LAYOUT_RESOURCE)
                                            R.layout.ps_custom_fragment_preview else InjectResourceSource.DEFAULT_LAYOUT_RESOURCE
                                    }
                                    .startActivityPreview(0, false, listPic)
                            }
                            binding.linroot.setOnClickListener {
                                hideSoftKeyBoard(this@ChatActivity)
                            }

                        }
                        //左边视频
                        R.layout.item_chat_video_left -> {//右边图片
                            var binding = getBinding<ItemChatVideoLeftBinding>()
                            var matchBeanNew = _data as MsgBeanData
                            if(0==layoutPosition){
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,context.dp2px(20))
                                binding.linroot.layoutParams = layoutParams
                            }else{
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,0)
                                binding.linroot.layoutParams = layoutParams
                            }

                            timecurrent = ChatTimeUtile.formatTimestamp(
                                context,
                                matchBeanNew.createTime!!.toLong()
                            )!!
                            binding.tvtime.text = timecurrent
                            if (bindingAdapterPosition < (adapter.models!!.size - 1)) {
                                var adnext =
                                    adapter.models!![bindingAdapterPosition + 1] as MsgBeanData
                                var nexttime = ChatTimeUtile.formatTimestamp(
                                    context,
                                    adnext.createTime!!.toLong()
                                )
                                if (timecurrent == nexttime) {
                                    binding.tvtime.visibility = View.GONE
                                } else {
                                    if (TimeUtil.isTimeDifferenceGreaterThan15Minutes(
                                            matchBeanNew.createTime!!, adnext.createTime!!

                                        )
                                    ) {
                                        if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                            matchBeanNew.lastShowTimeStamp = -1
                                        }
                                        lastShowTimeStamp = matchBeanNew.createTime!!
                                        binding.tvtime.visibility = View.VISIBLE
                                    } else {
                                        binding.tvtime.visibility = View.GONE
                                    }
                                }
                            } else {
                                binding.tvtime.visibility = View.VISIBLE
                            }
                            if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                matchBeanNew.lastShowTimeStamp = lastShowTimeStamp
                            }

                            if(matchBeanNew.content.isNotEmpty()){
                                val parts = matchBeanNew.content.split("***")
                                if(parts.size>=2){
                                    binding.ivpic.loadImageWithGlide(context, parts[0]){
                                        binding.ivVideoLogo.visibility=View.VISIBLE
                                    }
                                }else{
                                    binding.ivpic.loadImageWithGlide(context, matchBeanNew.content){
                                        binding.ivVideoLogo.visibility=View.VISIBLE
                                    }
                                }


                            }else {
                                binding.ivpic.loadImageWithGlide(context, matchBeanNew.content) {
                                    binding.ivVideoLogo.visibility = View.VISIBLE
                                }
                            }



                            Glide.with(this@ChatActivity).load(userhead)
                                .placeholder(R.drawable.default_anchor_icon).into(binding.ivhead)
                            binding.ivpic.setOnClickListener {

                                if(matchBeanNew.content.isNotEmpty()){
                                    val parts = matchBeanNew.content.split("***")
                                    if(parts.size>=2){
                                        VideoPlayActivity.open(videoUrl=parts[1])
                                    }
                                }



                            }
                            binding.linroot.setOnClickListener {
                                hideSoftKeyBoard(this@ChatActivity)
                            }

                        }
                        //右边视频
                        R.layout.item_chat_video_right-> {//右边图片
                            var binding = getBinding<ItemChatVideoRightBinding>()
                            var matchBeanNew = _data as MsgBeanData
                            if(0==layoutPosition){
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,context.dp2px(20))
                                binding.linroot.layoutParams = layoutParams
                            }else{
                                val layoutParams =
                                    binding.linroot.layoutParams as ViewGroup.MarginLayoutParams
                                layoutParams.setMargins(0,0,0,0)
                                binding.linroot.layoutParams = layoutParams
                            }
                            when (matchBeanNew.sentNew) {
                                0 -> {//正在发送
                                    LogUtils.d(
                                        bindingAdapterPosition.toString() + "准备发送" +
                                                JSONObject.toJSONString(_data as MsgBeanData)
                                    )
                                    addDataToList("4", (_data as MsgBeanData))
                                    binding.googleProgress.visibility = View.VISIBLE
                                    binding.ivfaile.visibility = View.GONE
                                    GlobalScope.launch {

                                        upLoadPic(
                                            bindingAdapterPosition,
                                            matchBeanNew,
                                            binding.tvpross,
                                            binding.ivfaile
                                        )
                                        delay(delayTime)

                                        if (matchBeanNew.sentNew == 0) {//发送失败
                                            matchBeanNew.sentNew = 2

                                            addDataToList("5", matchBeanNew)
//                                            if (bindingAdapterPosition == 0) {
//                                                appViewModel.updateMsgListEvent.postValue(
//                                                    matchBeanNew
//                                                )
//                                            }

                                            runOnUiThread {
                                                binding.googleProgress.visibility = View.GONE
                                                binding.ivfaile.visibility = View.VISIBLE



                                            }

                                        } else {//发送成功
                                            (_data as MsgBeanData).sentNew = 1

                                        }

                                    }
                                }

                                1, 3 -> {//发送成功
                                    (_data as MsgBeanData).sentNew = 1
                                    //  addDataToList("6", (_data as MsgBeanData))
                                    binding.googleProgress.visibility = View.GONE
                                    binding.ivfaile.visibility = View.GONE
                                    LogUtils.d(
                                        bindingAdapterPosition.toString() + "99发送chenggong" +
                                                JSONObject.toJSONString(_data as MsgBeanData)
                                    )
                                }

                                2 -> {//发送失败
                                    binding.googleProgress.visibility = View.GONE
                                    binding.ivfaile.visibility = View.VISIBLE
                                }

                                else -> {
                                    binding.googleProgress.visibility = View.GONE
                                    binding.ivfaile.visibility = View.GONE
                                }

                            }
                            binding.ivfaile.setOnClickListener {
                                reSendMsg(
                                    matchBeanNew,
                                    bindingAdapterPosition
                                )

                            }
                            timecurrent = ChatTimeUtile.formatTimestamp(
                                context,
                                matchBeanNew.createTime!!.toLong()
                            )!!
                            binding.tvtime.text = timecurrent
                            if (bindingAdapterPosition < (adapter.models!!.size - 1)) {
                                var adnext =
                                    adapter.models!![bindingAdapterPosition + 1] as MsgBeanData
                                var nexttime = ChatTimeUtile.formatTimestamp(
                                    context,
                                    adnext.createTime!!.toLong()
                                )
                                if (timecurrent == nexttime) {
                                    binding.tvtime.visibility = View.GONE
                                } else {
                                    if (TimeUtil.isTimeDifferenceGreaterThan15Minutes(
                                            matchBeanNew.createTime!!, adnext.createTime!!

                                        )
                                    ) {
                                        if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                            matchBeanNew.lastShowTimeStamp = -1
                                        }
                                        lastShowTimeStamp = matchBeanNew.createTime!!
                                        binding.tvtime.visibility = View.VISIBLE
                                    } else {
                                        binding.tvtime.visibility = View.GONE
                                    }
                                }
                            } else {
                                binding.tvtime.visibility = View.VISIBLE
                            }
                            if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                matchBeanNew.lastShowTimeStamp = lastShowTimeStamp
                            }



                            if (binding.ivhead.tag == null) {
                                binding.ivhead.tag = CacheUtil.getUser()?.head
                                Glide.with(this@ChatActivity).load(CacheUtil.getUser()?.head)
                                    .placeholder(R.drawable.icon_login_my_head)
                                    .into(binding.ivhead)
                            }
                            if (binding.ivpic.tag == null || binding.ivpic.tag != matchBeanNew.content) {
                                binding.ivpic.tag = matchBeanNew.content
//                                Glide.with(context)
//                                    .asBitmap()
//                                    .load(R.drawable.chat_icon_placeholder)
//                                    .placeholder(R.drawable.chat_icon_placeholder)
//                                    .error(R.drawable.chat_icon_placeholder)
//                                    .thumbnail(0.1f)
//                                    .skipMemoryCache(false)
                                if(matchBeanNew.content.isNotEmpty()){
                                    val parts = matchBeanNew.content.split("***")
                                    if(parts.size>=2){
                                        binding.ivpic.loadImageWithGlide(context, parts[0]){
                                            binding.ivVideoLogo.visibility=View.VISIBLE
                                        }
                                    }else{
                                        binding.ivpic.loadImageWithGlide(context, matchBeanNew.content){
                                            binding.ivVideoLogo.visibility=View.VISIBLE
                                        }
                                    }


                                }else{
                                    binding.ivpic.loadImageWithGlide(context, matchBeanNew.content){
                                        binding.ivVideoLogo.visibility=View.VISIBLE
                                    }
                                }

                            }


                            binding.ivpic.setOnClickListener {
                                if(matchBeanNew.content.isNotEmpty()){
                                    val parts = matchBeanNew.content.split("***")
                                    if(parts.size>=2){
                                        VideoPlayActivity.open(videoUrl=parts[1])
                                    }
                                }
                            }

                            binding.linroot.setOnClickListener {
                                hideSoftKeyBoard(this@ChatActivity)
                            }

                        }
                    }


                }

            }.models = listdata

            setOnclickNoRepeat(
                mDatabind.ivexpent,
                mDatabind.linphote,
                mDatabind.lincanmer,
                mDatabind.ivsend,
                mDatabind.ivface
            ) {
                when (it.id) {
                    R.id.ivexpent -> {
                        var hd = 45f
                        if (isShowBottom) {
                            hd = 0f
                        }
                        startAnmila(hd,1)
                    }
                    R.id.ivface->{
                        startAnmila(0f,2)


                    }

                    R.id.linphote -> {
                        if (Constants.ISSTOP_TALK != "0") {
                            myToast(resources.getString(R.string.str_stoptalk))
                            return@setOnclickNoRepeat
                        }
                        PictureCacheManager.deleteAllCacheDirRefreshFile(this);//清除图库缓存产生的临时文件

                        PictureSelector.create(this)
                            .openGallery(SelectMimeType.ofImage())
                            //  .setCropEngine(ImageFileCropEngine())
                            .setCompressEngine(ImageFileCompressEngine())
                            .setMaxSelectNum(1)
                            .setImageEngine(GlideEngine.createGlideEngine())
                            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                                override fun onResult(result: ArrayList<LocalMedia?>) {
                                    for (localMedia in result) {
                                        var path: String = ""
                                        Log.i(TAG, "onActivityResult: $path")
                                        if (localMedia?.compressPath != null) {
                                            path = localMedia?.compressPath!!
                                        } else {
                                            path = localMedia?.realPath!!
                                        }
                                        mDatabind.ivexpent.performClick()
                                        if (!TextUtils.isEmpty(path)) {
                                            msgType = 1
                                            msgContent = path
                                            sendMsg("", false)

                                        }
                                    }
                                }

                                override fun onCancel() {

                                }
                            })
                    }

                    R.id.lincanmer -> {
                        if (Constants.ISSTOP_TALK != "0") {
                            myToast(resources.getString(R.string.str_stoptalk))
                            return@setOnclickNoRepeat
                        }
                        PictureSelector.create(this)

                            .openCamera(SelectMimeType.ofImage())
                            .setCompressEngine(ImageFileCompressEngine())
                            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                                override fun onResult(result: ArrayList<LocalMedia?>) {
                                    for (localMedia in result) {
                                        var path: String = ""
                                        Log.i(TAG, "onActivityResult: $path")
                                        if (localMedia?.compressPath != null) {
                                            path = localMedia?.compressPath!!
                                        } else {
                                            path = localMedia?.realPath!!
                                        }
                                        mDatabind.ivexpent.performClick()
                                        if (!TextUtils.isEmpty(path)) {
                                            msgType = 1
                                            msgContent = path
                                            sendMsg("", false)

                                        }
                                    }
                                }

                                override fun onCancel() {}
                            })

                    }

                    R.id.ivsend -> {

                        if (mDatabind.edtcontent.text.toString().isNotEmpty()) {
                            msgType = 0
                            msgContent = mDatabind.edtcontent.text.toString()
                            sendMsg("", true)
                        } else {

                        }


                    }
                }
            }
            mDatabind.edtcontent.imeOptions = EditorInfo.IME_ACTION_SEND
            mDatabind.edtcontent.setRawInputType(InputType.TYPE_CLASS_TEXT)
            mDatabind.edtcontent.maxLines = 3
            mDatabind.edtcontent.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER))
                    || (actionId == EditorInfo.IME_ACTION_SEND)
                ) {
                    // 在这里执行相应的操作
                    val searchText = v.text.toString()
                    if (searchText.isNotEmpty() && searchText.trim().isNotEmpty()) {
                        msgType = 0
                        msgContent = searchText
                        sendMsg("", true)
                    } else {
                        myToast(resources.getString(R.string.detail_txt_input))
                    }
                    true // 返回 true 表示已处理事件
                } else false
                // 返回 false 表示未处理事件
            })


            MyWsManager.getInstance(this)?.setC2CListener(javaClass.name, object : C2CListener {

                override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {

                }

                override fun onSystemMsgReceive(chat: FeedSystemNoticeBean) {

                }

                override fun onC2CReceive(chat: ReceiveChatMsg) {
                    // mViewModel.clearMsg(userId)
                    mDatabind.state.showContent()
                    if (chat.from != CacheUtil.getUser()?.id) {//收到消息
                        var beanmy: MsgBeanData = MsgBeanData()
                        beanmy.anchorId = chat.anchorId
                        beanmy.fromId = chat.from
                        beanmy.content = chat.content
                        beanmy.chatType = chat.chatType
                        beanmy.cmd = 11
                        beanmy.msgType = chat.msgType
                        beanmy.createTime = chat.createTime
                        var listdata1: MutableList<MsgBeanData> = ArrayList<MsgBeanData>()
                        listdata1.add(beanmy)
                        mDatabind.rv.addModels(listdata1, index = 0)
                        mDatabind.rv.scrollToPosition(0)
                        MyWsManager.getInstance(this@ChatActivity)?.sendMessage(
                            Gson().toJson(
                                ReadWsBean(
                                    23, chat.id, 1, CacheUtil.getUser()?.id!!,
                                    3, chat.from
                                )
                            )
                        )

                        if(mDatabind.rv.models!=null){

                            if(CacheUtil.getUser()!=null){
                                var data = (mDatabind.rv.models as ArrayList<MsgBeanData>).reversed()
                                var liatNew=  data.takeLast(50) as ArrayList<MsgBeanData>
                                CacheDataList.globalCache[CacheUtil.getUser()!!.id+liatNew[0].anchorId!!]=liatNew

                            }

                        }
                        //addDataToList(beanmy)

                    } else {//发送消息  最后发送成功
                        // LogUtils.d("发送成功一条数据"+JSONObject.toJSONString(chat))
                        var sendDate=MsgBeanData()
                        var isShow:Int=-1
                        for (i in 0 until mDatabind.rv.models!!.size) {
                            var beanmy: MsgBeanData = mDatabind.rv.models!![i] as MsgBeanData
                            if (beanmy.sendId == chat.sendId) {
                                beanmy.sentNew = 1
                                beanmy.id = chat.id
                                addDataToList("7", beanmy)
                                sendDate=beanmy
                                isShow=i
                                mDatabind.rv.bindingAdapter.notifyItemChanged(i)
                                break
//

                            }
                        }

                        if(isShow!=-1){
                            //是重新发送
                            if(sendDate.againSend==1){
                                mDatabind.rv.mutable.remove(sendDate)
                                mDatabind.rv.bindingAdapter.notifyItemRemoved(isShow) // 通知更新
                                var listdata1: MutableList<MsgBeanData> = ArrayList<MsgBeanData>()
                                listdata1.add(sendDate)
                                mDatabind.rv.addModels(listdata1, index = 0)
                                mDatabind.rv.scrollToPosition(0)
                            }
                        }



                        if(mDatabind.rv.models!=null){
                          var data = (mDatabind.rv.models as ArrayList<MsgBeanData> ).reversed()
                          var liatNew=  data.takeLast(50) as  ArrayList<MsgBeanData>

                            if(CacheUtil.getUser()!=null){
                                CacheDataList.globalCache[CacheUtil.getUser()!!.id+liatNew[0].anchorId!!]=liatNew
                            }

                        }

                    }


                }

                override fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>) {

                }

            })

            // mViewModel.clearMsg(userId)
            initData()

            mViewModel.getUserInfo(userId)
            //获取本地数据
            getAllData()
            mViewModel.getHisMsgList(mDatabind.smartCommon, mViewModel.offset, userId)
        } catch (e: Exception) {
        }


        setDate()
    }
// 将Drawable转换为Bitmap

    @SuppressLint("SuspiciousIndentation")
    fun getAllData() {
        if(CacheUtil.getUser()!=null){
            try {
                GlobalScope.launch {

                    var data= CacheDataList.globalCache[CacheUtil.getUser()!!.id+ userId]
                    if(data!=null){
                        val reversedList = data.reversed()
                        runOnUiThread {
                            listdata.addAll(reversedList)
                            mDatabind.state.showContent()
                            mDatabind.rv.bindingAdapter.notifyDataSetChanged()
                        }
                    }else{
                        runOnUiThread {
                            LogUtils.d("私聊无数据缓存")
                            mDatabind.state.showEmpty()
                        }
                    }

//                val data = seacherData().await()
//                if (data.size > 0) {
//                    for (i in 0 until data.size) {
//                        if (data[i].sent == 0) {
//                            data[i].sent = 1
//                        }
//                    }
//
//                    listdata.addAll(data)
//                    runOnUiThread {
//                        mDatabind.rv.models = listdata
//                        mDatabind.state.showContent()
//                    }
//
//                } else {
//                    runOnUiThread {
//                        LogUtils.d("私聊无数据缓存")
//                        mDatabind.state.showEmpty()
//                    }
//
//                }

                }
            } catch (e: Exception) {
                Log.e("Val","获取的本地数据错误")
            }


        }

    }

    /**
     * type 1 是图片  2是emoje
     */
    fun startAnmila(hd: Float,type:Int) {
//        val rotationYAnimator =
//            ObjectAnimator.ofFloat(mDatabind.ivexpent, "rotation", 0f, hd)
//        rotationYAnimator.duration = 50
//
//        rotationYAnimator.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator) {
//                super.onAnimationEnd(animation)
//                if (isShowBottom) {
//                    isShowBottom = false
//                    mDatabind.linbottom.visibility = View.GONE
//                } else {
//                    isShowBottom = true
//                    mDatabind.linbottom.visibility = View.VISIBLE
//                }
//
//            }
//        })


//        rotationYAnimator.start()
        //type 1 是图片  2是emoje
//        if (isShowBottom) {
//            isShowBottom = false
//            mDatabind.linbottom.visibility = View.GONE
//        } else {
//            isShowBottom = true
//            mDatabind.linbottom.visibility = View.VISIBLE
//        }

//        if(isKeyboardOpen(this)) {
//            closeKeyboardNew(this)
//        }


            if(isShowBottom){//现在打开了下面的布局11     type 1 是图片  2是emoje
                if(type==1){
                    //如果是已经在显示了那就是整个多选都隐藏
                    if(mDatabind.llShowImage.visibility==View.VISIBLE){
                        mDatabind.linbottom.visibility = View.GONE
                        isShowBottom = false
                    }else{
                        mDatabind.llShowImage.visibility=View.VISIBLE
                        mDatabind.rvEmoje.visibility=View.GONE
                    }

                }else{//
                    //如果是已经在显示了那就是整个多选都隐藏
                    if(mDatabind.rvEmoje.visibility==View.VISIBLE){
                        mDatabind.linbottom.visibility = View.GONE
                        isShowBottom = false
                    }else{
                        mDatabind.llShowImage.visibility=View.GONE
                        mDatabind.rvEmoje.visibility=View.VISIBLE
                    }


                }


            }else{//是关闭状态
                mDatabind.linbottom.visibility = View.VISIBLE
                //2是emoje
                if(type==1){
                    mDatabind.llShowImage.visibility=View.VISIBLE
                    mDatabind.rvEmoje.visibility=View.GONE
                }else{
                    mDatabind.llShowImage.visibility=View.GONE
                    mDatabind.rvEmoje.visibility=View.VISIBLE
                }

                isShowBottom = true
            }





//        if(isShowBottom){
//            isShowBottom = false
//            mDatabind.linbottom.visibility = View.GONE
//            mDatabind.llShowImage.visibility=View.VISIBLE
//            mDatabind.rvEmoje.visibility=View.GONE
//        } else {
//            isShowBottom = true
//            mDatabind.linbottom.visibility = View.VISIBLE
//            if(type==1){
//                mDatabind.llShowImage.visibility=View.VISIBLE
//                mDatabind.rvEmoje.visibility=View.GONE
//            }else{
//                mDatabind.llShowImage.visibility=View.GONE
//                mDatabind.rvEmoje.visibility=View.VISIBLE
//
//            }



//        }

    }

    /**
     * 上传图片
     */
    suspend fun upLoadPic(index: Int,bean: MsgBeanData, view: TextView, image: ImageView) {
        try {

            val file = File(bean.content)

            runOnUiThread { view.visibility = View.VISIBLE }
            val requestBody = CountingRequestBody(file, "image/*", object :
                ProgressListener {
                override fun onProgress(bytesWritten: Long, contentLength: Long) {
                    // 在此处更新进度
                    val progress = 100.0 * bytesWritten / contentLength
                    runOnUiThread {
                        view.text = progress.toInt().toString() + "%"
                        LogUtils.d("Upload progress: ${progress.toInt()}" + "%")
                        if (progress.toInt() > 99) {
                            view.visibility = View.GONE
                        }
                    }

                }
            })

            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
            mViewModel.upLoadPicSuspend(multipartBody) {
                LogUtils.d("Upload progress:$it")
                if (it != "FAILE") {
                    msgType = 1
                    msgContent = it
                    sendMsg(bean.sendId!!, true)
                } else {
                    runOnUiThread {
                        view.visibility = View.GONE
                        image.visibility = View.VISIBLE

                    }
                    if (bean.sentNew == 0){//发送失败
                        bean.sentNew = 2

                        addDataToList("5", bean)
//                        if (index == 0) {
//                            appViewModel.updateMsgListEvent.postValue(bean)
//                        }
                    }
                    bean.sentNew = 2
                    addDataToList("8", bean)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun initLongClick(view: View, content: String, firstVisiblePosition: Int) {
        try {

            val layoutManager = mDatabind.rv.layoutManager
            if (layoutManager != null && layoutManager is LinearLayoutManager) {
                val firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition)
                val topDistance = firstVisibleView?.top
                // 使用 topDistance 进行你需要的操作
                LogUtils.d(firstVisiblePosition.toString() + "顶部距离===$topDistance")
                var gragt = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                if (topDistance!! > 0) {
                    gragt = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                } else {
                    gragt = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

                }
                CustomDialog.show(object :
                    OnBindView<CustomDialog>(R.layout.layout_custom_dialog_align) {
                    private var btnSelectPositive: TextView? = null
                    private var tvup: ImageView? = null
                    private var tvdown: ImageView? = null
                    override fun onBind(dialog: CustomDialog, v: View) {
                        tvup = v.findViewById<ImageView>(R.id.img_bkg_up)
                        tvdown = v.findViewById<ImageView>(R.id.img_bkg)
                        if (topDistance!! > 0) {
                            tvup!!.visibility = View.GONE
                        } else {
                            tvdown!!.visibility = View.GONE

                        }
                        btnSelectPositive = v.findViewById<TextView>(R.id.btn_selectPositive)
                        btnSelectPositive!!.setOnClickListener(View.OnClickListener {
                            copyToClipboard(content)
                            ToastUtils.showShort(resources.getString(R.string.copy_success))
                            dialog.dismiss()
                        })
                    }
                })
                    .setCancelable(true)
                    .setMaskColor(resources.getColor(R.color.translet))
//            .setEnterAnimResId(R.anim.anim_custom_pop_enter)
//            .setExitAnimResId(R.anim.anim_custom_pop_exit)
                    .setAlignBaseViewGravity(view, gragt)
                    // .setBaseViewMarginBottom(-dip2px(45f))
                    .show()
            }

        } catch (e: Exception) {
        }
    }

    fun initData() {
        mDatabind.smartCommon.setOnRefreshListener {
            mViewModel.getHisMsgList(mDatabind.smartCommon, mViewModel.offset, userId)
        }
    }


    fun setDate(){
        val resourceId =  R.raw.emoji // 替换成你的 JSON 文件名
        val jsonString = appContext.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        val jsonObject = org.json.JSONObject(jsonString)
        val keysList = ArrayList<String>()
        var count = 0
        jsonObject.keys().forEach { key ->
            if (count < 80) {
                keysList.add(key)
                count++
            }
        }

//        jsonObject.keys().forEach { key ->
//            keysList.add(key)
//        }

        mDatabind.rvEmoje.grid(8).setup {
            addType<String>(R.layout.item_emoje_txt)
            onBind {
                var bindingItem=getBinding<ItemEmojeTxtBinding>()
                var  bean=_data as String

                val spannable = SpannableString(bean)
                spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.c_37373d)), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                bindingItem.txtEmoje.text=spannable

                bindingItem.txtEmoje.setOnClickListener {

                    if (CacheUtil.isLogin()&& Constants.ISSTOP_TALK.equals("0")) {
                        var  bean=_data as String
                        val spannable = SpannableString(bean)
                        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.c_37373d)), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        mDatabind.edtcontent.setText(mDatabind.edtcontent.text.toString()+ spannable)
                        mDatabind.edtcontent.setSelection(mDatabind.edtcontent.text.length)
                    }

                }


            }
//           R.id.txtEmoje.onClick {
//
//               if (CacheUtil.isLogin()&& Constants.ISSTOP_TALK.equals("0")) {
//                   var  bean=_data as String
//                   val spannable = SpannableString(bean)
//                   spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.c_37373d)), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                    mDatabind.edtcontent.setText(mDatabind.edtcontent.text.toString()+ spannable)
//                   mDatabind.edtcontent.setSelection(mDatabind.edtcontent.text.length)
//                }
//
//           }

        }.addModels(keysList)
    }

    override fun createObserver() {
        super.createObserver()

        mViewModel.emojiList.observe(this){

            mDatabind.rvEmoje.grid(8).setup {
                addType<String>(R.layout.item_emoje_txt)
                onBind {
                    var bindingItem=getBinding<ItemEmojeTxtBinding>()
                    var  bean=_data as String
                    bindingItem.txtEmoje.text=bean

                }
            }.addModels(it)
        }

        try {


            mViewModel.upPic.observe(this) {
                if (it.isNotEmpty()) {
                    msgContent = it

                }
            }
            mViewModel.autherInfo.observe(this) {
                if (it != null) {
                    nickname = it.nickName
                    mDatabind.titleTop.tvTitle.text = nickname
                }
            }
            mViewModel.hisMsgList.observeForever {
                      //第一页
                    if(mViewModel.isRefresh){
                            if(it.size>0){
                                var data= it.reversed()
                                listdata.clear()
                                listdata.addAll(data)
                                mDatabind.state.showContent()
                                mDatabind.rv.bindingAdapter.notifyDataSetChanged()
                            }else{
                                if(listdata.size>0){
                                    mViewModel.offset=listdata[listdata.size-1].id!!
                                }
                            }


                    }else{
                        if(it.size>0){
                            var data= it.reversed()
                            listdata.addAll(listdata.size,data)
//                            mDatabind.rv.addModels(data, index =  0)
                            mDatabind.rv.bindingAdapter.notifyDataSetChanged()
                        }
                    }


//                if (it.size > 0) {
//
//                    if (listdata.size == 0) {//没有本地数据
//                        var i = 0
//                        var j: Int = it.size - 1
//                        while (i < j) {
//                            val temp: MsgBeanData = it[i]
//                            it[i] = it[j]
//                            it[j] = temp
//                            i++
//                            j--
//                        }
//                        listdata.addAll(it)
//                        mDatabind.state.showContent()
//                        for (i in 0 until it.size) {
//                            if (it[i].sendId == "0") {
//                                it[i].sendId = userId + it[i].createTime
//                            }
//                            it[i].sent = 1
//
//                            addDataToList("9", it[i])
//                        }
//                    } else {
//
//                        var bean = listdata[0]
//                        for ((index, data) in it.withIndex()) {
//
//
//                            val foundData = listdata.find { it.id == data.id }
//                            if (foundData == null) {
//
//                                data?.let { it1 ->
//
//                                    if (TimeUtil.isEarlier(
//                                            data.createTime!!,
//                                            bean.createTime!!
//                                        )
//                                    ) {
//                                        if (it1.sendId == "0") {
//                                            it1.sendId = it1.id
//                                        }
//                                        incount++
//                                        data.sent = 1
//                                        addDataToList("10", data)
//                                        var listdata1: MutableList<MsgBeanData> =
//                                            ArrayList<MsgBeanData>()
//                                        listdata1.add(data)
//                                        mDatabind.rv.addModels(listdata1, index = 0)
//                                        mDatabind.rv.scrollToPosition(0) // 保证最新一条消息显示
//
//                                    }
//                                }
//                            }
//                        }
////                    runBlocking {
////                        var ss = waitAddData(it)
////                        getAllData()
////                    }
//
//                    }
//                }

                // mDatabind.rv.addModels(it)
                mDatabind.smartCommon.setEnableRefresh(true)

//                if (it.size < 10) {
//                    mDatabind.smartCommon.setEnableRefresh(false)
//                }else{
//                    mDatabind.smartCommon.setEnableRefresh(true)
//                }

            }
        } catch (e: Exception) {
        }



    }


    fun sendMsg(sendid: String, isSend: Boolean,timeold:Long=0) {
        try {
            if (Constants.ISSTOP_TALK != "0") {
                myToast(resources.getString(R.string.str_stoptalk))
                return
            }
            val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            var timestamp = fmt.format(Date(System.currentTimeMillis()))
            val date: Date = fmt.parse(timestamp)
            var creatime :Long= 0
            if (timeold>0){
                creatime = timeold
            }else{
                creatime = date.time
            }

            var sendID = ""
            if (sendid.isEmpty()) {
                sendID = userId + creatime
            } else {
                sendID = sendid
            }

            var bean = SendChatMsgBean(
                2,
                msgType,
                11,
                CacheUtil.getUser()?.id,
                userId,
                userId,
                msgContent,
                "0",
                creatime,
                sendID,
                if (userhead.isEmpty()) "" else userhead,
                if (nickname.isEmpty()) "" else nickname,
                if (CacheUtil.getUser()?.head!!.isEmpty()) "" else CacheUtil.getUser()?.head,
                if (CacheUtil.getUser()?.name!!.isEmpty()) "" else CacheUtil.getUser()?.name,
                CacheUtil.getUser()?.lvNum
            )
            if (isSend) {
                MyWsManager.getInstance(this)?.sendMessage(
                    Gson().toJson(bean)
                )
            }
            if (sendid.isEmpty()) {
                mDatabind.edtcontent.setText("")
                var beanmy: MsgBeanData = MsgBeanData()
                beanmy.anchorId = userId
                beanmy.fromId = bean.from
                beanmy.content = bean.content!!
                beanmy.chatType = 2
                beanmy.nick = nickname
                beanmy.avatar = userhead
                beanmy.id = sendID
                beanmy.sendId = sendID
                beanmy.cmd = 11
                beanmy.sentNew = 0
                beanmy.msgType = bean.msgType
                beanmy.createTime = creatime
                var listdata1: MutableList<MsgBeanData> = ArrayList<MsgBeanData>()
                listdata1.add(beanmy)
                mDatabind.state.showContent()
//                if (isShowBottom) {
//                    mDatabind.ivexpent.performClick()
//                }
                if (isShowBottom) {
                    isShowBottom=false
                    mDatabind.linbottom.visibility=View.GONE
                }

                mDatabind.rv.addModels(listdata1, index = 0)
                mDatabind.rv.scrollToPosition(0) // 保证最新一条消息显示

            }
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        MyWsManager.getInstance(this)?.removeC2CListener(javaClass.name)
        appViewModel.updateMsgEvent.postValue("-2")
        super.onDestroy()
    }

    fun addData(data: MutableList<MsgBeanData>) {
        GlobalScope.launch {

            for (i in 0 until data.size) {
                MyApplication.dataBase!!.chatDao?.insert(data[i])
            }


        }
    }

    /***
     * 添加或者更新新的数据
     */
    fun addDataToList(index: String, data: MsgBeanData) {
        LogUtils.d(index + "嘿嘿开始添加数据" + JSONObject.toJSONString(data))

//        GlobalScope.launch {
//            mutex.withLock {
//                if (CacheUtil.getUser() != null) {
//                    data.withId = CacheUtil.getUser()?.id!!
//                    MyApplication.dataBase!!.chatDao?.insertOrUpdate(data)
//                }
//            }
//        }
    }

    fun seacherData(): Deferred<MutableList<MsgBeanData>> {
        return GlobalScope.async {

            MyApplication.dataBase!!.chatDao?.getMessagesByName(userId, CacheUtil.getUser()?.id!!)!!
        }
    }

    fun hideSoftKeyBoard(activity: Activity) {
        val localView = activity.currentFocus
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (localView != null && imm != null) {
            imm.hideSoftInputFromWindow(localView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun reSendMsg(
        matchBeanNew: MsgBeanData,
        index: Int
    ) {
        try {

            if (Constants.ISSTOP_TALK != "0") {
                myToast(resources.getString(R.string.str_stoptalk))
                return
            }
            reSendMsgDialog(this) { isSure ->
                matchBeanNew.sentNew = 0
                //是重新发送
                matchBeanNew.againSend=1
                if (matchBeanNew.msgType == 0) {
                    msgType = matchBeanNew.msgType!!
                    msgContent = matchBeanNew.content
                    sendMsg(matchBeanNew.id!!, true)
                }
                mDatabind.rv.bindingAdapter.notifyItemChanged(index)
            }

        } catch (e: Exception) {
        }
    }
//    private val mLayoutManager by lazy {
//
//
//        HoverLinearLayoutManager(this, RecyclerView.VERTICAL, false).apply {
//            // stackFromEnd = true
//        }
//    }
//    /**
//     * 判断软件是否打开
//     */
//    fun isKeyboardOpen(context: Context): Boolean {
//        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        return inputMethodManager.isAcceptingText
//    }

    /**
     * 关闭软键盘
     */
    fun closeKeyboard(view: View, context: Context) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isKeyboardOpen(activity: Activity): Boolean {
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = activity.window.decorView.height
        val keypadHeight = screenHeight - rect.bottom
        return keypadHeight > screenHeight * 0.15 // 根据实际需求设定阈值
    }


    fun closeKeyboardNew(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity.currentFocus
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}