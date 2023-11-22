package com.xcjh.app.ui.chat

import android.R.string
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.manager.PictureCacheManager
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.databinding.ActivityChatBinding
import com.xcjh.app.databinding.ItemChatPicLeftBinding
import com.xcjh.app.databinding.ItemChatPicRightBinding
import com.xcjh.app.databinding.ItemChatTxtLeftBinding
import com.xcjh.app.databinding.ItemChatTxtRightBinding
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.GlideEngine
import com.xcjh.app.utils.nice.Utils
import com.xcjh.app.utils.picture.ImageFileCompressEngine
import com.xcjh.app.websocket.MyWsManager
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
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import java.io.File


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
    var listdata: MutableList<MsgBean> = ArrayList<MsgBean>()
    var msgType = 0//消息类型，文字：0， 图片：1
    var msgContent = ""
    private val listPic = java.util.ArrayList<LocalMedia>()

    override fun initView(savedInstanceState: Bundle?) {

        options = RequestOptions()
            .transform(CenterCrop(), RoundedCorners(Utils.dp2px(this, 8f)))


        ImmersionBar.with(this)
            .statusBarDarkFont(false)
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
        // 键盘弹出平滑动画
        userId = intent.getStringExtra(Constants.USER_ID) ?: ""
        nickname = intent.getStringExtra(Constants.USER_NICK) ?: ""
        userhead = intent.getStringExtra(Constants.USER_HEAD) ?: ""
        appViewModel.updateMsgEvent.postValue(userId)
        mDatabind.titleTop.tvTitle.text = nickname
        Glide.with(this).load(userhead).placeholder(R.drawable.default_anchor_icon)
            .into(mDatabind.titleTop.ivhead)

        mDatabind.rv.setup {
            addType<MsgBean> {
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
                        var ad = _data as MsgBean

                        binding.tvcontent.text = ad.content
                        binding.tvtime.text =
                            TimeUtil.timeStamp2Date(ad.createTime!!, null)!!.substring(0,16)
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


                    }

                    R.layout.item_chat_txt_right -> {//正在进行中的比赛
                        var binding = getBinding<ItemChatTxtRightBinding>()
                        var matchBeanNew = _data as MsgBean
                        binding.tvtime.text =
                            TimeUtil.timeStamp2Date(matchBeanNew.createTime!!, null)!!.substring(0,16)
                        binding.tvtime.visibility = View.GONE
                        binding.tvcontent.text =
                            matchBeanNew.content
                        Glide.with(this@ChatActivity).load(CacheUtil.getUser()?.head)
                            .placeholder(R.drawable.icon_avatar)
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

                    }

                    R.layout.item_chat_pic_right -> {//右边图片
                        var binding = getBinding<ItemChatPicRightBinding>()
                        var matchBeanNew = _data as MsgBean
                        binding.tvtime.text =
                            TimeUtil.timeStamp2Date(matchBeanNew.createTime!!, null)!!.substring(0,16)
                        binding.tvtime.visibility = View.GONE
                        Glide.with(this@ChatActivity).load(CacheUtil.getUser()?.head)
                            .placeholder(R.drawable.icon_avatar)
                            .into(binding.ivhead)
                        Glide.with(this@ChatActivity).load(matchBeanNew.content).dontAnimate()
                            .into(binding.ivpic)
                        binding.ivpic.setOnClickListener {
                            listPic.clear()
                            var localMedia: LocalMedia = LocalMedia()
                            localMedia?.path = matchBeanNew.content
                            localMedia?.cutPath = matchBeanNew.content
                            listPic.add(localMedia)
                            PictureSelector.create(this@ChatActivity)
                                .openPreview()
                                .setImageEngine(GlideEngine.createGlideEngine())
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


                    }

                    R.layout.item_chat_pic_left -> {//右边图片
                        var binding = getBinding<ItemChatPicLeftBinding>()
                        var matchBeanNew = _data as MsgBean
                        binding.tvtime.text =
                            TimeUtil.timeStamp2Date(matchBeanNew.createTime!!, null)!!.substring(0,16)
                        binding.tvtime.visibility = View.GONE
                        Glide.with(this@ChatActivity).load(matchBeanNew.content)
                            .dontAnimate().into(binding.ivpic)
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
                                .setImageEngine(GlideEngine.createGlideEngine())
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

                R.id.linphote -> {
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
                                    if (!TextUtils.isEmpty(path)) {
                                        mViewModel.upLoadPic(File(path))

                                        msgType = 1

                                    }
                                }
                            }

                            override fun onCancel() {

                            }
                        })
                }

                R.id.lincanmer -> {


                }

                R.id.ivsend -> {

                    if (mDatabind.edtcontent.text.toString().isNotEmpty()) {
                        msgType = 0
                        msgContent = mDatabind.edtcontent.text.toString()
                        sendMsg()
                    } else {

                    }


                }
            }
        }
        MyWsManager.getInstance(this)?.setC2CListener(javaClass.name, object : C2CListener {

            override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {

            }

            override fun onC2CReceive(chat: ReceiveChatMsg) {
                mViewModel.clearMsg(userId)
                var beanmy: MsgBean = MsgBean()
                beanmy.fromId = chat.from
                beanmy.content = chat.content
                beanmy.chatType = 2
                beanmy.cmd = 11
                beanmy.msgType = chat.msgType
                beanmy.createTime = System.currentTimeMillis()
                var listdata1: MutableList<MsgBean> = ArrayList<MsgBean>()
                listdata1.add(beanmy)
                mDatabind.rv.addModels(listdata1, index = 0)
                mDatabind.rv.scrollToPosition(0) // 保证最新一条消息显示

                //appViewModel.updateMsgEvent.postValue(beanmy)

            }

            override fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>) {

            }

        })
        mViewModel.clearMsg(userId)
        initData()
        mViewModel.getHisMsgList(mDatabind.smartCommon, offset, userId)
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
                sendMsg()
            }
        }
        mViewModel.hisMsgList.observeForever {

            var i = 0
            var j: Int = it.size - 1
            while (i < j) {
                val temp: MsgBean = it[i]
                it[i] = it[j]
                it[j] = temp
                i++
                j--
            }
            mDatabind.rv.addModels(it)
            offset = it[it.size - 1].id!!

            if (it.size < 50) {
                mDatabind.smartCommon.setEnableRefresh(false)
            }


        }

    }

    fun initMyMsg() {
        var beanmy: MsgBean = MsgBean()
        beanmy.fromId = CacheUtil.getUser()?.id
        beanmy.anchorId = userId
        beanmy.content = msgContent
        beanmy.avatar = userhead
        beanmy.chatType = 2
        beanmy.nick = nickname
        beanmy.cmd = 11
        beanmy.msgType = msgType
        beanmy.createTime = System.currentTimeMillis()
        var listdata1: MutableList<MsgBean> = ArrayList<MsgBean>()
        listdata1.add(beanmy)
        mDatabind.rv.addModels(listdata1, index = 0)
        // mDatabind.recBottom.smoothScrollToPosition(listdata.size - 1)
        mDatabind.rv.scrollToPosition(0) // 保证最新一条消息显示


        //  appViewModel.updateMsgEvent.postValue(beanmy)

    }

    fun sendMsg() {
        // initMyMsg()

        var bean = SendChatMsgBean(
            2,
            msgType,
            11,
            CacheUtil.getUser()?.id,
            userId,
            userId,
            msgContent,
            "0",
            System.currentTimeMillis(),
            CacheUtil.getUser()?.id,
            if (userhead.isEmpty()) "" else userhead,
            if (nickname.isEmpty()) "" else nickname,
            if (CacheUtil.getUser()?.head!!.isEmpty()) "" else CacheUtil.getUser()?.head,
            if (CacheUtil.getUser()?.name!!.isEmpty()) "" else CacheUtil.getUser()?.name,
            CacheUtil.getUser()?.lvNum
        )
        MyWsManager.getInstance(this)?.sendMessage(
            Gson().toJson(bean)
        )
        mDatabind.edtcontent.setText("")
    }

    override fun onDestroy() {
        MyWsManager.getInstance(this)?.removeC2CListener(javaClass.name)
        appViewModel.updateMsgEvent.postValue("0")
        super.onDestroy()
    }
}