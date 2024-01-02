package com.xcjh.app.ui.chat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
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
import com.xcjh.app.ui.room.MsgBeanData
import com.xcjh.app.utils.CacheUtil
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
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.TAG
import com.xcjh.base_lib.utils.TimeUtil
import com.xcjh.base_lib.utils.copyToClipboard
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    var offset = ""
    var userhead = ""
    var isShowBottom = false
    var lastShowTimeStamp: Long = 0
    val baseLong: Long = 0
    var listdata: MutableList<MsgBeanData> = ArrayList<MsgBeanData>()
    var msgType = 0//消息类型，文字：0， 图片：1
    var msgContent = ""
    var isUpdata = false
    private val delayTime: Long = 10000
    private val listPic = java.util.ArrayList<LocalMedia>()
    private val mutex = Mutex()
    var incount=0
    override fun initView(savedInstanceState: Bundle?) {

        options = RequestOptions()
            .transform(CenterCrop(), RoundedCorners(Utils.dp2px(this, 8f)))



        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .keyboardEnable(true)
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

                        binding.tvcontent.text = ad.content
                        binding.tvtime.text =
                            TimeUtil.timeStamp2Date(ad.createTime!!, null)!!.substring(0, 16)
                        binding.tvtime.visibility = View.GONE
                        Glide.with(this@ChatActivity).load(userhead)
                            .placeholder(R.drawable.default_anchor_icon).into(binding.ivhead)
                        if (ad.lastShowTimeStamp!! == baseLong) {
                            ad.lastShowTimeStamp = lastShowTimeStamp
                        }
                        if (TimeUtil.getTimeCha(
                                ad.createTime, ad.lastShowTimeStamp

                            ) == true
                        ) {
                            if (ad.lastShowTimeStamp!! == baseLong) {
                                ad.lastShowTimeStamp = -1
                            }
                            lastShowTimeStamp = ad.createTime!!
                            binding.tvtime.visibility = View.VISIBLE
                        }
                        binding.tvcontent.setOnLongClickListener(OnLongClickListener {
                            initLongClick(binding.tvcontent, ad.content)
                            true
                        })
                        binding.linroot.setOnClickListener {
                            hideSoftKeyBoard(this@ChatActivity)
                        }

                    }

                    R.layout.item_chat_txt_right -> {//正在进行中的比赛
                        var binding = getBinding<ItemChatTxtRightBinding>()
                        var matchBeanNew = _data as MsgBeanData

                        when (matchBeanNew.sent) {
                            0 -> {//正在发送

                                addDataToList("1", matchBeanNew)

                                binding.googleProgress.visibility = View.VISIBLE
                                binding.ivfaile.visibility = View.GONE
                                GlobalScope.launch {
                                    delay(delayTime)
                                    LogUtils.d(
                                        "发送成功或者失败" + JSONObject.toJSONString(
                                            matchBeanNew
                                        )
                                    )
                                    if (matchBeanNew.sent == 0) {//发送失败
                                        matchBeanNew.sent = 2
                                        addDataToList("2", matchBeanNew)
                                        if (bindingAdapterPosition == 0) {

                                            appViewModel.updateMsgListEvent.postValue(matchBeanNew)
                                        }
                                        runOnUiThread {
                                            binding.googleProgress.visibility = View.GONE
                                            binding.ivfaile.visibility = View.VISIBLE
                                        }

                                    } else {//发送成功
                                        matchBeanNew.sent = 1

                                    }

                                }
                            }

                            1, 3 -> {//发送成功

                                matchBeanNew.sent = 1
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
                        binding.tvtime.text =
                            TimeUtil.timeStamp2Date(matchBeanNew.createTime!!, null)!!
                                .substring(0, 16)
                        binding.tvtime.visibility = View.GONE
                        binding.tvcontent.text =
                            matchBeanNew.content
                        Glide.with(this@ChatActivity).load(CacheUtil.getUser()?.head)
                            .placeholder(R.drawable.icon_login_my_head)
                            .into(binding.ivhead)
                        if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                            matchBeanNew.lastShowTimeStamp = lastShowTimeStamp
                        }
                        if (TimeUtil.getTimeCha(
                                matchBeanNew.createTime, matchBeanNew.lastShowTimeStamp

                            ) == true
                        ) {
                            if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                matchBeanNew.lastShowTimeStamp = -1
                            }
                            lastShowTimeStamp = matchBeanNew.createTime!!
                            binding.tvtime.visibility = View.VISIBLE
                        }
                        binding.tvcontent.setOnLongClickListener(OnLongClickListener {
                            initLongClick(binding.tvcontent, matchBeanNew.content)
                            true
                        })
                        binding.linroot.setOnClickListener {
                            hideSoftKeyBoard(this@ChatActivity)
                        }

                    }

                    R.layout.item_chat_pic_right -> {//右边图片
                        var binding = getBinding<ItemChatPicRightBinding>()
                        var matchBeanNew = _data as MsgBeanData

                        when (matchBeanNew.sent) {
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
                                        matchBeanNew,
                                        binding.tvpross,
                                        binding.ivfaile
                                    )
                                    delay(delayTime)
                                    LogUtils.d(
                                        bindingAdapterPosition.toString() + "99检查发送" +
                                                JSONObject.toJSONString(_data as MsgBeanData)
                                    )
                                    if ((_data as MsgBeanData).sent == 0) {//发送失败
                                        (_data as MsgBeanData).sent = 2
                                        LogUtils.d(
                                            bindingAdapterPosition.toString() + "99发送失败" +
                                                    JSONObject.toJSONString(_data as MsgBeanData)
                                        )
                                        addDataToList("5", (_data as MsgBeanData))
                                        if (bindingAdapterPosition == 0) {
                                            appViewModel.updateMsgListEvent.postValue((_data as MsgBeanData))
                                        }
                                        runOnUiThread {
                                            binding.googleProgress.visibility = View.GONE
                                            binding.ivfaile.visibility = View.VISIBLE
                                        }

                                    } else {//发送成功
                                        (_data as MsgBeanData).sent = 1

                                    }

                                }
                            }

                            1, 3 -> {//发送成功
                                (_data as MsgBeanData).sent = 1
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
                        binding.tvtime.text =
                            TimeUtil.timeStamp2Date(matchBeanNew.createTime!!, null)!!
                                .substring(0, 16)
                        binding.tvtime.visibility = View.GONE


                        if (binding.ivhead.tag == null) {
                            binding.ivhead.tag = CacheUtil.getUser()?.head
                            Glide.with(this@ChatActivity).load(CacheUtil.getUser()?.head)
                                .placeholder(R.drawable.icon_login_my_head)
                                .into(binding.ivhead)
                        }
                        if (binding.ivpic.tag == null || binding.ivpic.tag != matchBeanNew.content) {
                            binding.ivpic.tag = matchBeanNew.content
                            binding.ivpic.loadImageWithGlide(context, matchBeanNew.content)
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
                        if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                            matchBeanNew.lastShowTimeStamp = lastShowTimeStamp
                        }
                        if (TimeUtil.getTimeCha(
                                matchBeanNew.createTime, matchBeanNew.lastShowTimeStamp

                            ) == true
                        ) {
                            if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                matchBeanNew.lastShowTimeStamp = -1
                            }
                            lastShowTimeStamp = matchBeanNew.createTime!!
                            binding.tvtime.visibility = View.VISIBLE

                        }
                        binding.linroot.setOnClickListener {
                            hideSoftKeyBoard(this@ChatActivity)
                        }

                    }

                    R.layout.item_chat_pic_left -> {//右边图片
                        var binding = getBinding<ItemChatPicLeftBinding>()
                        var matchBeanNew = _data as MsgBeanData
                        binding.tvtime.text =
                            TimeUtil.timeStamp2Date(matchBeanNew.createTime!!, null)!!
                                .substring(0, 16)
                        binding.tvtime.visibility = View.GONE
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
                        if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                            matchBeanNew.lastShowTimeStamp = lastShowTimeStamp
                        }
                        if (TimeUtil.getTimeCha(
                                matchBeanNew.createTime, matchBeanNew.lastShowTimeStamp

                            ) == true
                        ) {
                            if (matchBeanNew.lastShowTimeStamp!! == baseLong) {
                                matchBeanNew.lastShowTimeStamp = -1
                            }
                            lastShowTimeStamp = matchBeanNew.createTime!!
                            binding.tvtime.visibility = View.VISIBLE

                        }
                    }
                }


            }

        }.models = listdata
        setOnclickNoRepeat(
            mDatabind.ivexpent,
            mDatabind.linphote,
            mDatabind.lincanmer,
            mDatabind.ivsend
        ) {
            when (it.id) {
                R.id.ivexpent -> {
                    var hd = 45f
                    if (isShowBottom) {
                        hd = 0f
                    }
                    startAnmila(hd)


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
        mDatabind.edtcontent.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_SEND)
            ) {
                // 在这里执行相应的操作
                val searchText = v.text.toString()
                if (searchText.isNotEmpty()&&searchText.length>0) {
                    msgType = 0
                    msgContent = searchText
                    sendMsg("", true)
                }else{
                    myToast(resources.getString(R.string.str_inputcontent))
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
                    //addDataToList(beanmy)

                } else {//发送消息
                    // LogUtils.d("发送成功一条数据"+JSONObject.toJSONString(chat))
                    for (i in 0 until mDatabind.rv.models!!.size) {
                        var beanmy: MsgBeanData = mDatabind.rv.models!![i] as MsgBeanData
                        if (beanmy.sendId == chat.sendId) {
                            beanmy.sent = 1
                            beanmy.id = chat.id
                            LogUtils.d(
                                i.toString() + "1发送成功一条数据" + JSONObject.toJSONString(
                                    beanmy
                                )
                            )
                            addDataToList("7", beanmy)
                            mDatabind.rv.bindingAdapter.notifyItemChanged(i)
                            break
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

        getAllData()
        mViewModel.getHisMsgList(mDatabind.smartCommon, offset, userId)

    }
// 将Drawable转换为Bitmap

    fun getAllData() {
        GlobalScope.launch {
            val data = seacherData().await()
            if (data.size > 0) {
                for (i in 0 until data.size) {
                    if (data[i].sent == 0) {
                        data[i].sent = 1
                    }
                }
                LogUtils.d("私聊有数据缓存" + JSONObject.toJSONString(data))
                listdata.addAll(data)
                runOnUiThread {
                    mDatabind.rv.models = listdata
                    mDatabind.state.showContent()
                }

            } else {
                runOnUiThread {
                    LogUtils.d("私聊无数据缓存")
                    mDatabind.state.showEmpty()
                }

            }

        }
    }

    fun startAnmila(hd: Float) {
        val rotationYAnimator =
            ObjectAnimator.ofFloat(mDatabind.ivexpent, "rotation", 0f, hd)
        rotationYAnimator.duration = 50

        rotationYAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (isShowBottom) {
                    isShowBottom = false
                    mDatabind.linbottom.visibility = View.GONE
                } else {
                    isShowBottom = true
                    mDatabind.linbottom.visibility = View.VISIBLE
                }

            }
        })
        rotationYAnimator.start()
    }

    suspend fun upLoadPic(bean: MsgBeanData, view: TextView, image: ImageView) {
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
                        if (progress.toInt() >99) {
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
                    view.visibility = View.GONE
                    image.visibility = View.VISIBLE
                    bean.sent = 2
                    addDataToList("8", bean)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun initLongClick(view: View, content: String) {
        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.layout_custom_dialog_align) {
            private var btnSelectPositive: TextView? = null
            override fun onBind(dialog: CustomDialog, v: View) {
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
            .setAlignBaseViewGravity(view, Gravity.TOP or Gravity.CENTER_HORIZONTAL)
            // .setBaseViewMarginBottom(-dip2px(45f))
            .show()
    }

    fun initData() {
        mDatabind.smartCommon.setOnRefreshListener {
            mViewModel.getHisMsgList(mDatabind.smartCommon, offset, userId)
        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.upPic.observe(this) {
            if (it.isNotEmpty()) {
                msgContent = it

            }
        }
        mViewModel.autherInfo.observe(this){
            if (it!=null){
                nickname=it.nickName
                mDatabind.titleTop.tvTitle.text = nickname
            }
        }
        mViewModel.hisMsgList.observeForever {
            if (it.size > 0) {

                if (listdata.size == 0) {//没有本地数据
                    var i = 0
                    var j: Int = it.size - 1
                    while (i < j) {
                        val temp: MsgBeanData = it[i]
                        it[i] = it[j]
                        it[j] = temp
                        i++
                        j--
                    }
                    listdata.addAll(it)
                    mDatabind.state.showContent()
                    for (i in 0 until it.size) {
                        if (it[i].sendId == "0") {
                            it[i].sendId = userId + it[i].createTime
                        }
                        it[i].sent=1

                        LogUtils.d("昵称是===" +it[i].nick)
                        addDataToList("9", it[i])
                    }
                } else {
                    LogUtils.d("有新的消息===" + JSONObject.toJSONString(it))
                    var bean=listdata[0]
                    for ((index, data) in it.withIndex()) {

                        LogUtils.d("昵称是===" +data.nick)
                        val foundData = listdata.find { it.id == data.id }
                        if (foundData == null) {

                            data?.let { it1 ->
                                LogUtils.d("第一个数据===" + JSONObject.toJSONString(bean))
                                if (TimeUtil.isEarlier(
                                        data.createTime!!,
                                        bean.createTime!!
                                    )
                                ) {
                                    if (it1.sendId == "0") {
                                        it1.sendId =it1.id+incount.toString()
                                    }
                                    incount++
                                    data.sent = 1
                                    addDataToList("10", data)
                                    var listdata1: MutableList<MsgBeanData> =
                                        ArrayList<MsgBeanData>()
                                    listdata1.add(data)
                                    mDatabind.rv.addModels(listdata1, index = 0)
                                    mDatabind.rv.scrollToPosition(0) // 保证最新一条消息显示
                                    LogUtils.d("有新的消息需要加入缓存===" + data.content)
                                }
                            }
                        }
                    }
//                    runBlocking {
//                        var ss = waitAddData(it)
//                        getAllData()
//                    }

                }
            }

            // mDatabind.rv.addModels(it)
            //offset = it[it.size - 1].id!!


            if (it.size < 50) {
                mDatabind.smartCommon.setEnableRefresh(false)
            }

        }

    }


    fun sendMsg(sendid: String, isSend: Boolean) {
        if (Constants.ISSTOP_TALK != "0") {
            myToast(resources.getString(R.string.str_stoptalk))
            return
        }
        val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        var timestamp = fmt.format( Date(System.currentTimeMillis()))
        val date: Date = fmt.parse(timestamp)
        val creatime = date.time
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
            beanmy.sent = 0
            beanmy.msgType = bean.msgType
            beanmy.createTime = creatime
            var listdata1: MutableList<MsgBeanData> = ArrayList<MsgBeanData>()
            listdata1.add(beanmy)
            mDatabind.state.showContent()
            if (isShowBottom) {
                mDatabind.ivexpent.performClick()
            }

            mDatabind.rv.addModels(listdata1, index = 0)
            mDatabind.rv.scrollToPosition(0) // 保证最新一条消息显示

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

        GlobalScope.launch {
            mutex.withLock {
                if (CacheUtil.getUser() != null) {
                    data.withId = CacheUtil.getUser()?.id!!
                    MyApplication.dataBase!!.chatDao?.insertOrUpdate(data)
                }
            }
        }
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
        if (Constants.ISSTOP_TALK != "0") {
            myToast(resources.getString(R.string.str_stoptalk))
            return
        }
        reSendMsgDialog(this) { isSure ->
            matchBeanNew.sent = 0
            if (matchBeanNew.msgType == 0) {
                msgType = matchBeanNew.msgType!!
                msgContent = matchBeanNew.content
                sendMsg(matchBeanNew.id!!, true)
            }
            mDatabind.rv.bindingAdapter.notifyItemChanged(index)

        }
    }

}