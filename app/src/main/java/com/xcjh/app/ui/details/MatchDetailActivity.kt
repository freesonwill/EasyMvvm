package com.xcjh.app.ui.details


import android.annotation.SuppressLint
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.android.cling.ClingDLNAManager
import com.android.cling.control.DeviceControl
import com.android.cling.control.OnDeviceControlListener
import com.android.cling.control.ServiceActionCallback
import com.android.cling.entity.ClingDevice
import com.android.cling.entity.ClingPlayType
import com.android.cling.startBindUpnpService
import com.android.cling.stopUpnpService
import com.android.cling.util.Utils
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ImmersionBar.getStatusBarHeight
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.xcjh.app.R
import com.xcjh.app.adapter.ViewPager2Adapter
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.AnchorListBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.databinding.ActivityMatchDetailBinding
import com.xcjh.app.isTopActivity
import com.xcjh.app.net.ApiComService
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.ui.details.common.GSYBaseActivity
import com.xcjh.app.ui.details.fragment.*
import com.xcjh.app.utils.*
import com.xcjh.app.utils.TimeUtil
import com.xcjh.app.view.PopupSelectProjection
import com.xcjh.app.view.balldetail.ControlShowListener
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.listener.LiveStatusListener
import com.xcjh.app.websocket.listener.OtherPushListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.*
import com.xcjh.base_lib.utils.view.clickNoRepeat
import com.xcjh.base_lib.utils.view.visibleOrGone
import com.xcjh.base_lib.utils.view.visibleOrInvisible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.fourthline.cling.model.meta.Device
import java.math.BigDecimal
import java.util.*
import kotlin.math.abs

/**
 * 比赛详情 主页
 */

class MatchDetailActivity :
    GSYBaseActivity<DetailVm, ActivityMatchDetailBinding, StandardGSYVideoPlayer>() {

    private var mTitles = arrayListOf<String>()
    private var mFragList = arrayListOf<Fragment>()
    private lateinit var pager2Adapter: ViewPager2Adapter
    private lateinit var matchDetail: MatchDetailBean //当前比赛详情
    private var isNeedInit: Boolean = true //是否需要初始化

    private var matchType: String = "1" //比赛类型(1：足球；2：篮球)
    private var matchId: String = ""//比赛id
    private var matchName: String? = "" //
    private var anchor: AnchorListBean? = null //当前主播详情
    private var anchorId: String? = null //主播ID
    private var isHasAnchor: Boolean = false //当前流是否有主播
    private var isShowVideo: Boolean = false //当前是否播放视频
    //保存异常状态
    private var errStatic:Int=0

    // private var playUrl: String? = "rtmp://liteavapp.qcloud.com/live/liteavdemoplayerstreamid"
    // private var playUrl: String? = "https://sf1-hscdn-tos.pstatp.com/obj/media-fe/xgplayer_doc_video/flv/xgplayer-demo-720p.flv"
    //选择的播放设备
    private var selectDevice: ClingDevice? = null

    //播放失败
    private var deviceErr: Device<*, *, *>? = null

    //弹出搜索框
    private var popup: PopupSelectProjection? = null

    //投屏需要的对象
    private var control: DeviceControl? = null
    private var mUpnpServiceConnection: ServiceConnection? = null

    //当前界面在顶部
    private var topActivity: Boolean = true

    companion object {
        fun open(
            matchType: String = "1",
            matchId: String,
            matchName: String? = "",
            anchorId: String? = null,
            videoUrl: String? = null,
        ) {
            startNewActivity<MatchDetailActivity> {
                putExtra("matchType", matchType)
                putExtra("matchId", matchId)
                putExtra("matchName", matchName)
                putExtra("anchorId", anchorId)
                putExtra("videoUrl", videoUrl)

            }
        }
    }

    fun dataPopup() {
        popup = PopupSelectProjection(this)
        var popwindow = XPopup.Builder(this)
            .hasShadowBg(false)
            .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
            .isViewMode(true)
            .isDestroyOnDismiss(true)
            .asCustom(popup).show()
        //选择投屏的时候不允许横屏
        this.setIsLandscape(false)

        popup!!.popupSelectProjectionListener =
            object : PopupSelectProjection.PopupSelectProjectionListener {
                override fun clickClose() {

                }

                override fun clickDevice(date: ClingDevice) {
                    popwindow!!.dismiss()

                    if (selectDevice != null && selectDevice!!.name.equals(date.name)) {
                        control?.setAVTransportURI(
                            mDatabind.videoPlayer.getUrl(),
                            "",
                            ClingPlayType.TYPE_VIDEO,
                            object :
                                ServiceActionCallback<Unit> {
                                override fun onSuccess(result: Unit) {
//                                "投放成功".showToast()
                                    control?.play() //有些还要重新调用一次播放
                                }

                                override fun onFailure(msg: String) {
//                                "投放失败:$msg".showToast()
                                    myToast("链接失败")
                                }
                            })
                    } else {
                        selectDevice = date
                        control = ClingDLNAManager.getInstant().connectDevice(date, object :
                            OnDeviceControlListener {
                            override fun onConnected(device: Device<*, *, *>) {
                                super.onConnected(device)
//                        myToast("连接成功")
                                if (control == null) {
                                    myToast("设备被清除请从新选择")
                                    return
                                }

                                control?.setAVTransportURI(
                                    mDatabind.videoPlayer.getUrl(),
                                    "",
                                    ClingPlayType.TYPE_VIDEO,
                                    object :
                                        ServiceActionCallback<Unit> {
                                        override fun onSuccess(result: Unit) {
//                                "投放成功".showToast()
                                            control?.play() //有些还要重新调用一次播放
                                        }

                                        override fun onFailure(msg: String) {
//                                "投放失败:$msg".showToast()
                                            myToast("链接失败")
                                        }
                                    })

                            }

                            override fun onDisconnected(device: Device<*, *, *>) {
                                super.onDisconnected(device)
                                Log.i("SSSSSSSSSCCCC", "=" + device)
//                            myToast("无法连接")
                                deviceErr = device
                                ClingDLNAManager.getInstant().disconnectDevice(device)
                            }
                        })
                    }


                    control?.addControlObservers()
                }

                override fun onDisappear() {
                    setIsLandscape(true)
                }

            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this).statusBarDarkFont(false)//白色
            .navigationBarColor(R.color.c_181819)
            .navigationBarDarkIcon(false)
            .titleBarMarginTop(mDatabind.rltTop).init()
        // 使用方法
        mDatabind.videoPlayer.setShrinkImageRes(R.drawable.detaic_tv_icon_cioss);
        mDatabind.videoPlayer.setEnlargeImageRes(R.drawable.detaic_tv_icon_screen);

//        mDatabind.videoPlayer!!.setFullScreenCover("11111")
        mDatabind.ivMatchVideo.clickNoRepeat {
            ClingDLNAManager.getInstant().searchDevices()
            dataPopup()
        }
        if (anchorId != null) {
            mDatabind.ivMatchVideo.visibility = View.VISIBLE
        }

        mDatabind.videoPlayer.isRotateWithSystem = false
        //横屏分享 1 是分享  2是投屏 3选择信号源
        appViewModel.landscapeShareEvent.observe(this) {
            if (topActivity) {
                if (it == 1) {
                    GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
                        setShareDate()
                    }
                } else if (it == 2) {//
                    GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
                        delay(1000L) // 延迟1秒（1000毫秒）
                        ClingDLNAManager.getInstant().searchDevices()
                        dataPopup()
                    }

                } else if (it == 3) {

                    showSignal()
                }

            }


        }
//        mDatabind.videoPlayer.matchVideoListener=object : MatchVideoPlayer.MatchVideoListener{
//            override fun setShare() {
//                setShareDate()
//            }
//
//            override fun screen() {
//                ClingDLNAManager.getInstant().searchDevices()
//                dataPopup()
//            }
//
//        }

        //        mDatabind.mediaRouteButton.SessionManagerListene
//        mDatabind.mediaRouteButton.sets
        //解决toolbar左边距问题
        mDatabind.toolbar.setContentInsetsAbsolute(0, 0)
        mDatabind.viewTopBg.layoutParams.height = getStatusBarHeight(this)
        mDatabind.toolbar.layoutParams.height = getStatusBarHeight(this) + dip2px(44f)
        mViewModel.tt = 5
        intent.extras?.apply {
            matchType = getString("matchType", "1")
            matchId = getString("matchId", "0")
            matchName = getString("matchName", "")
            anchorId = getString("anchorId", null)
            //  playUrl = getString("videoUrl", null)
            isHasAnchor = !anchorId.isNullOrEmpty()
            setData()
            /* FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                 if (!task.isSuccessful) {
                     "Fetching FCM registration token failed===${task.exception}".loge("push====token===")
                     return@OnCompleteListener
                 }

                 // Get new FCM registration token
                 val token = task.result

                 // Log and toast
                 val msg = token
                 msg.loge("push====token===")
             })*/
        }
        initStaticUI()
        initVp()
        initOther()
        // setTestTab()

    }


    private fun setData() {
        mViewModel.getMatchDetail(matchId, matchType, true)
        if (isHasAnchor) {
            // mViewModel.getDetailAnchorInfo(anchorId)
        }
    }

    private fun initStaticUI() {
        //进入界面需要传：比赛类型(1：足球；2：篮球)、比赛ID、比赛名称、主队客队名称头像、比赛时间、状态、公告、在线视频，后面改成传bean
        //比赛名称
        mDatabind.topLiveTitle.text = matchName ?: ""

        //比赛类型
        if (matchType == "1") {
            mDatabind.ivMatchBg.setBackgroundResource(R.drawable.bg_status_football)
            mDatabind.toolbar.setBackgroundResource(if (isHasAnchor) R.color.translet else R.drawable.bg_top_football)
        } else {
            mDatabind.ivMatchBg.setBackgroundResource(R.drawable.bg_status_basketball)
            mDatabind.toolbar.setBackgroundResource(if (isHasAnchor) R.color.translet else R.drawable.bg_top_basketball)
        }


        startTimeAnimator(mDatabind.tvMatchTimeS)
        mDatabind.apply {
            topLiveTitle.visibleOrGone(isHasAnchor)
        }
    }

    private fun showHideLive(isClose: Boolean = false) {
        mDatabind.tvToShare.visibleOrGone(true)
        mDatabind.apply {
            if (isClose) {
                rltVideo.visibleOrGone(false)
                cslMatchInfo.visibleOrGone(false)
                lltNoLive.visibleOrGone(true)
                topLiveTitle.visibleOrGone(true)
                mDatabind.rltTop.background.alpha = 0
                topNoLiveTitle.visibleOrGone(false)
                lltLiveError.visibleOrGone(false)
            } else {
                lltNoLive.visibleOrGone(false)
                //有视频布局
                rltVideo.visibleOrGone(isShowVideo)
                topLiveTitle.visibleOrGone(isShowVideo)
                mDatabind.rltTop.background.alpha = if (isShowVideo) 255 else 0
                viewTopBg.visibleOrGone(isHasAnchor)
                //无视频纯净流布局
                cslMatchInfo.visibleOrGone(!isShowVideo)
                topNoLiveTitle.visibleOrGone(!isShowVideo)
                lltLiveError.visibleOrGone(false)
            }
        }
    }

    private fun initVp() {

        mDatabind.viewPager.initChangeActivity(this, mFragList, true)
        pager2Adapter = mDatabind.viewPager.adapter as ViewPager2Adapter
        //初始化Tab控件
        mDatabind.magicIndicator.bindMatchViewPager2(
            mDatabind.viewPager,
            mTitles,
            selectSize = 15f,
            unSelectSize = 14f,
            selectColor = R.color.c_ffffff,
            normalColor = R.color.c_94999f,
            typefaceBold = true,
            scrollEnable = true,
            lineIndicatorColor = R.color.c_34a853,
            margin = 18
        ) {
            if (it == 0) {
                setUnScroll(mDatabind.lltFold)
            } else {
                if (isShowVideo) {

                    setUnScroll(mDatabind.lltFold)
                } else {
                    setScroll(mDatabind.lltFold)
                }
            }
        }
    }


    private fun initOther() {


        ///跑马灯设置
        mDatabind.marqueeView.isSelected = true
        mDatabind.toolbar.background.alpha = 0
        mDatabind.rltTop.background.alpha = 255
        ///滑动监听
        mDatabind.appBayLayout.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (verticalOffset > 0) return
                val v = myDivide(abs(verticalOffset), appBarLayout.totalScrollRange)
                //折叠时隐藏的top布局
                mDatabind.layoutNotFold.alpha = 1 - v
                //折叠后显示top
                mDatabind.layoutTopFold.alpha = v
                mDatabind.toolbar.background.alpha = (v * 255).toInt()
                // mDatabind.tvSignal.isClickable = !(v < 2 && v > 0.8)
            }
        })
        initVideoBuilderMode()
        MyWsManager.getInstance(App.app)
            ?.setLiveStatusListener(this.toString(), object : LiveStatusListener {
                override fun onOpenLive(bean: LiveStatus) {
                    if (matchId == bean.matchId) {
                        if (anchor?.userId == bean.anchorId) {
                            isShowVideo = true
                            showHideLive()
                            //更新聊天
                            mViewModel.anchorInfo.value = AnchorListBean(
                                liveId = bean.id,
                                userId = bean.anchorId,
                                nickName = bean.nickName,
                                playUrl = bean.playUrl,
                                hotValue = bean.hotValue.toString()
                            )
                            anchor?.apply {
                                userId = bean.id ?: ""
                                nickName = bean.nickName ?: ""
                                playUrl = bean.playUrl ?: ""
                            }
                            matchDetail.anchorList?.forEach {
                                if (it.userId == bean.anchorId) {
                                    it.isOpen = true
                                    it.playUrl = bean.playUrl
                                }
                            }
                            if (isTopActivity(this@MatchDetailActivity) && !isPause) {
                                startVideo(bean.playUrl)
                            }
                        } else {
                            //增加主播
                            var add = true
                            matchDetail.anchorList?.forEach {
                                if (it.userId == bean.anchorId) {
                                    //在列表中 不用添加
                                    it.playUrl = bean.playUrl
                                    add = false
                                }
                            }
                            //在列表中没找到就添加
                            if (add) {
                                matchDetail.anchorList?.add(
                                    AnchorListBean(
                                        liveId = bean.id,
                                        userId = bean.anchorId,
                                        nickName = bean.nickName,
                                        playUrl = bean.playUrl,
                                        hotValue = bean.hotValue.toString()
                                    )
                                )
                            }
                            matchDetail.anchorList?.sortByDescending {
                                it.hotValue
                            }
                        }

                    }
                }

                override fun onCloseLive(bean: LiveStatus) {
                    //"onReceive========${bean.id}===${anchor?.liveId}".loge()
                    if (matchId == bean.matchId) {
                        if (anchor?.userId == bean.anchorId) {
                            mDatabind.videoPlayer.release()
                            GSYVideoManager.releaseAllVideos()
                            isShowVideo = false
                            showHideLive(true)
                            anchor?.isOpen = false
                            matchDetail.anchorList?.forEach {
                                it.isOpen = it.userId != bean.anchorId
                            }
//                            if( mDatabind.videoPlayer.isIfCurrentIsFullscreen){
//                                if(mDatabind.videoPlayer.currentPlayer.lltLiveErrorNew!=null){
//                                    mDatabind.videoPlayer.currentPlayer.lltLiveErrorNew.visibility=View.GONE
//                                    mDatabind.videoPlayer.currentPlayer.ivMatchBgNew.visibility=View.VISIBLE
//                                    mDatabind.videoPlayer.currentPlayer.lltNoLiveNew.visibility=View.VISIBLE
//                                }
//
//                            }
                            //关闭直播间的时候如果是横屏也是
                            if (mDatabind.videoPlayer.isIfCurrentIsFullscreen) {
                                exitFullScreen()
//                                isShowVideo = false
//                                showHideLive(true)
                                setIsLandscape(false)
                            }

                        } else {
                            val iterator = matchDetail.anchorList?.iterator()
                            if (iterator != null) {
                                for (tab in iterator) {
                                    if (tab.userId == bean.anchorId) {
                                        iterator.remove()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onChangeLive(bean: LiveStatus) {
                    if (matchId == bean.matchId) {
                        if (anchor?.userId == bean.anchorId) {
                            anchor?.playUrl = bean.playUrl
                            showHideLive()


                            if (isShowVideo) {
                                if (isTopActivity(this@MatchDetailActivity) && !isPause) {


                                    if (mDatabind.videoPlayer.isIfCurrentIsFullscreen) {
                                        exitFullScreen()
                                    }
                                    GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
                                        delay(1000L) // 延迟1秒（1000毫秒）
                                        startVideo(anchor?.playUrl)
//
                                    }
                                    //这个是横屏时候要处理
//                                    mDatabind.videoPlayer.currentPlayer.release();
//                                    GSYVideoManager.instance().releaseMediaPlayer();
//                                    mDatabind.videoPlayer.currentPlayer.setUp(anchor?.playUrl,false,"");
//                                    mDatabind.videoPlayer.currentPlayer.startPlayLogic();
                                }
                            }
                        } else {
                            matchDetail.anchorList?.forEach {
                                if (it.userId == bean.anchorId) {
                                    it.playUrl = bean.playUrl
                                }
                            }
                        }
                    }

                }
            })
        MyWsManager.getInstance(App.app)
            ?.setOtherPushListener(this.toString(), object : OtherPushListener {
                override fun onChangeMatchData(matchList: ArrayList<ReceiveChangeMsg>) {
                    try {
                        //防止数据未初始化的情况
                        if (::matchDetail.isInitialized && matchDetail.status in 0..if (matchType == "1") 7 else 9) {
                            matchList.forEach {
                                if (matchId == it.matchId.toString() && matchType == it.matchType.toString()) {
                                    Gson().toJson(it).loge("===66666===")
                                    matchDetail.apply {
                                        status = BigDecimal(it.status).toInt()
                                        if (matchType == "1") {
                                            if (it.status.toInt() == 2) {
                                                runTime = it.runTime.toInt()//上半场
                                            } else if (it.status.toInt() == 4) {
                                                runTime = it.runTime.toInt()//下半场
                                            }
                                        }
                                        awayHalfScore = BigDecimal(it.awayHalfScore).toInt()
                                        awayScore = BigDecimal(it.awayScore).toInt()
                                        homeHalfScore = BigDecimal(it.homeHalfScore).toInt()
                                        homeScore = BigDecimal(it.homeScore).toInt()
                                    }.apply {
                                        needWsToUpdateUI()
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.loge("e====e")
                    }
                }
            })
    }

    fun setShareDate() {
        //分享 固定地址
        //复制链接成功
        val url = if (isHasAnchor) {
            if (CacheUtil.isLogin()) {
                mViewModel.addLiveShare(anchor?.liveId)
            }
            ApiComService.SHARE_IP + "#/roomDetail?id=${matchId}&liveId=${anchor?.liveId}&type=${matchType}&userId=${anchor?.userId}"
        } else {
            ApiComService.SHARE_IP + "#/roomDetail?id=${matchId}&type=${matchType}&pureFlow=true"
        }
        /*  copyToClipboard(url)
          myToast(getString(R.string.copy_success))*/
        shareText(this, url)
    }

    private fun setBaseListener() {
        //分享按钮
        mDatabind.tvToShare.setOnClickListener {
            setShareDate()
        }
        //信号源
        mDatabind.tvSignal.setOnClickListener {
            showSignal()
        }
        mDatabind.tvSignal2.setOnClickListener {
            showSignal()
        }
        mDatabind.tvSignal3.setOnClickListener {
            showSignal()
        }
        mDatabind.tvSignal4.setOnClickListener {
            showSignal()
        }
    }

    private fun showSignal() {
        if (matchDetail.anchorList?.isNotEmpty() == true) {
            showSignalDialog(matchDetail.anchorList) { anchor, pos ->
                matchDetail.anchorList?.forEach {
                    it.isSelect = it.userId == anchor.userId
                }
                if (this.anchor?.userId == anchor.userId) {
                    //无改变
                    return@showSignalDialog
                }
                val iterator = matchDetail.anchorList?.iterator()
                if (iterator != null) {
                    for (tab in iterator) {
                        if (!tab.isOpen) {
                            iterator.remove()
                        }
                    }
                }
                //切换主播
                this.anchor = anchor
                //是纯净流
                if (anchor.pureFlow) {
                    mDatabind.ivMatchVideo.visibility = View.GONE
                    this.setIsLandscape(false)
                    isHasAnchor = false
                    isShowVideo = !anchor.playUrl.isNullOrEmpty()
                    // mDatabind.videoPlayer.currentState
                    if (mDatabind.videoPlayer.isIfCurrentIsFullscreen) {
                        exitFullScreen()
                    }

                } else {
                    mDatabind.ivMatchVideo.visibility = View.VISIBLE
//                    this.setIsLandscape(true)
                    isHasAnchor = true
                    isShowVideo = true
                }

                mDatabind.tvToShare.visibleOrGone(true)
                if (isShowVideo) {
                    startVideo(anchor.playUrl)
                } else {
                    mDatabind.videoPlayer.release()
                }
                changeUI()

            }
        } else {
            myToast("no data", isDeep = true)
        }
    }

    /**
     * 设置基础UI
     */
    private fun showBaseUI() {

        if (matchType == "1") {//足球
            //主队名称以及图标
            mDatabind.tvHomeName.text = matchDetail.homeName ?: ""
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.def_football)
                .into(mDatabind.ivHomeIcon)
            //客队名称以及图标
            mDatabind.tvAwayName.text = matchDetail.awayName
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.def_football)
                .into(mDatabind.ivAwayIcon)
        } else {
            //主队名称以及图标
            mDatabind.tvAwayName.text = "${matchDetail.homeName}\n(${resources.getString(R.string.my_app_name)})"
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.def_basketball)
                .into(mDatabind.ivAwayIcon)
            //客队名称以及图标
            mDatabind.tvHomeName.text = matchDetail.awayName
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.def_basketball)
                .into(mDatabind.ivHomeIcon)
        }
        //赛事名字和比赛时间
        mDatabind.tvCompetitionName.text = matchDetail.competitionName
        needWsToUpdateUI()
    }

    /**
     * 需要实时更新的UI
     */
    private fun needWsToUpdateUI() {
        //修改的
//        matchName = if (matchDetail.status in 2..if (matchType == "1") 8 else 10) {
        matchName = if (matchDetail.status in 2..if (matchType == "1") 10 else 10) {
            matchDetail.competitionName + "  " +
                    if (matchType == "1") {
                        "${matchDetail.homeName ?: ""} ${matchDetail.homeScore ?: ""}:${matchDetail.awayScore ?: ""} ${matchDetail.awayName ?: ""}"
                    } else "${matchDetail.awayName ?: ""}  ${matchDetail.awayScore ?: ""}:${matchDetail.homeScore ?: ""}${matchDetail.homeName ?: ""}"
        } else {
            matchDetail.competitionName + "  " +
                    if (matchType == "1") {
                        "${matchDetail.homeName ?: ""} VS ${matchDetail.awayName ?: ""}"
                    } else "${matchDetail.awayName ?: ""} VS ${matchDetail.homeName ?: ""}"
        }

        mDatabind.topLiveTitle.text = matchName
        //上滑停靠栏
        getMatchStatus(mDatabind.tvTopMatchStatus, matchDetail.matchType, matchDetail.status)
        //比赛状态
        getMatchStatus(mDatabind.tvMatchStatus, matchDetail.matchType, matchDetail.status)
        updateRunTime()
        //有比分的情况 足球status正在比赛是[2,8] 篮球是[2,10]
        if (matchType == "1") {
            //足球
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.def_football)
                .into(mDatabind.ivTopHomeIcon)
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.def_football)
                .into(mDatabind.ivTopAwayIcon)
            mDatabind.tvTopHomeScore.text =
                if (matchDetail.status in 2..8) matchDetail.homeScore.toString() else ""
            mDatabind.tvTopAwayScore.text =
                if (matchDetail.status in 2..8) matchDetail.awayScore.toString() else ""
            if (matchDetail.status in 2..8) {
                mDatabind.tvMatchVs.textSize = 20f
                mDatabind.tvMatchVs.text =
                    matchDetail.homeScore.toString() + "-" + matchDetail.awayScore.toString()
            } else {
                mDatabind.tvMatchVs.textSize = 22f
                mDatabind.tvMatchVs.text = getString(R.string.vs)
            }
        } else {
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.def_basketball)
                .into(mDatabind.ivTopHomeIcon)
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.def_basketball)
                .into(mDatabind.ivTopAwayIcon)
            mDatabind.tvTopHomeScore.text =
                if (matchDetail.status in 2..10) matchDetail.awayScore.toString() else ""
            mDatabind.tvTopAwayScore.text =
                if (matchDetail.status in 2..10) matchDetail.homeScore.toString() else ""
            if (matchDetail.status in 2..10) {
                mDatabind.tvMatchVs.textSize = 20f
                mDatabind.tvMatchVs.text =
                    matchDetail.awayScore.toString() + "-" + matchDetail.homeScore.toString()
            } else {
                mDatabind.tvMatchVs.textSize = 22f
                mDatabind.tvMatchVs.text = getString(R.string.vs)
            }
        }
    }

    private fun startVideo(url: String?) {
        //先停
        // stopVideo()
        //再开
        Log.i("SSSSSSSSSs","========"+url)
        mDatabind.videoPlayer.visibleOrGone(true)
        mDatabind.videoPlayer.setUp(url, false, "")
        mDatabind.videoPlayer.startPlayLogic()
        //如果要加上横屏就用这几个
//        mDatabind.videoPlayer.getCurrentPlayer().release()
//        GSYVideoManager.instance().releaseMediaPlayer()
//        mDatabind.videoPlayer.getCurrentPlayer().setUp(url, false, "")
//        mDatabind.videoPlayer.getCurrentPlayer().startPlayLogic()


        mDatabind.videoPlayer.setGSYStateUiListener {
            //it.toString().loge("======")
            Log.i("bobobobobo","================="+it)
            if (it == 2) {
//                this.setIsLandscape(true)
                if (!mDatabind.videoPlayer.isIfCurrentIsFullscreen) {

                    GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
                }

//                if( mDatabind.videoPlayer.isIfCurrentIsFullscreen){
//                    if(mDatabind.videoPlayer.currentPlayer.lltLiveErrorNew!=null){
//                        mDatabind.videoPlayer.currentPlayer.lltLiveErrorNew.visibility=View.GONE
//                        mDatabind.videoPlayer.currentPlayer.ivMatchBgNew.visibility=View.GONE
//                        mDatabind.videoPlayer.currentPlayer.lltNoLiveNew.visibility=View.GONE
//                    }
////                    mDatabind.apply {
////                        isShowVideo=true
////                        isHasAnchor=true
////                        lltNoLive.visibleOrGone(false)
////                        //有视频布局
////                        rltVideo.visibleOrGone(isShowVideo)
////                        topLiveTitle.visibleOrGone(isShowVideo)
////                        mDatabind.rltTop.background.alpha = if (isShowVideo) 255 else 0
////                        viewTopBg.visibleOrGone(isHasAnchor)
////                        //无视频纯净流布局
////                        cslMatchInfo.visibleOrGone(!isShowVideo)
////                        topNoLiveTitle.visibleOrGone(!isShowVideo)
////                        lltLiveError.visibleOrGone(false)
////
////                    }
//
//                }

//                 GSYVideoType.setScreenScaleRatio(mDatabind.videoPlayer.gsyVideoManager.currentVideoWidth / mDatabind.videoPlayer.gsyVideoManager.currentVideoHeight.toFloat())
//                GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_CUSTOM)
            }else if(it==3){

            }  else if (it == 7) {

                //如果是横屏的时候报错就竖屏
                if (mDatabind.videoPlayer.isIfCurrentIsFullscreen) {
////                    isShowVideo = false
//                    showHideLive(true)
                    mDatabind.videoPlayer.exitFullScreen()

                }

//                GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
//                    delay(1000L) // 延迟1秒（1000毫秒）
//
//
//                }
                this.setIsLandscape(false)
//                    if (isShowVideo) {
//                        //有视频布局  修改
//                        rltVideo.visibleOrGone(false)
//                        lltLiveError.visibleOrGone(true)
//                        tvReload.setOnClickListener {
//                            startVideo(anchor?.playUrl)
//                            showHideLive()
//                        }
//                    }
//                }


//                if( mDatabind.videoPlayer.isIfCurrentIsFullscreen){
//                    if(mDatabind.videoPlayer.currentPlayer.lltLiveErrorNew!=null){
//                        mDatabind.videoPlayer.currentPlayer.lltLiveErrorNew.visibility=View.VISIBLE
//                        mDatabind.videoPlayer.currentPlayer.ivMatchBgNew.visibility=View.VISIBLE
//                        mDatabind.videoPlayer.currentPlayer.lltNoLiveNew.visibility=View.GONE
//                    }
//
//
//

            }
            mDatabind.videoPlayer.playbackStatus(it)
        }
        mDatabind.videoPlayer.setControlListener(object : ControlShowListener {
            override fun onShow() {

                if (isShowVideo) {
                    mDatabind.rltTop.background.alpha = 255
                    mDatabind.topLiveTitle.visibleOrGone(true)
                    mDatabind.tvToShare.visibleOrGone(true)
                    mDatabind.tvSignal.visibleOrGone(true)
                    mDatabind.ivMatchVideo.visibleOrGone(true)

                }
            }

            override fun onHide() {

                if (isShowVideo) {
                    mDatabind.rltTop.background.alpha = 0
                    mDatabind.topLiveTitle.visibleOrGone(false)
                    mDatabind.tvToShare.visibleOrGone(false)
                    mDatabind.tvSignal.visibleOrGone(false)
                    mDatabind.ivMatchVideo.visibleOrGone(false)
                }
            }


        })


    }

    public fun closeLiveService() {
        //关闭投屏服务之类的
        ClingDLNAManager.stopLocalFileService(this)
        stopUpnpService(mUpnpServiceConnection)
        ClingDLNAManager.getInstant().destroy()

    }

    //数据处理
    override fun createObserver() {
        //比赛详情接口返回监听处理
        mViewModel.detail.observe(this) { match ->
            if (match != null) {
                matchDetail = match
                setBaseListener()
                showBaseUI()
                if (isNeedInit) {
                    isNeedInit = false
                    if (match.status in 2..if (matchType == "1") 7 else 9) {
                        mViewModel.startTimeRepeat(match.runTime)
                    }
                    ///判断当前是否展示直播
                    getAnchor {
                        mDatabind.root.postDelayed(
                            {
                                startVideo(it)
                            }, 10
                        )
                    }
                    changeUI()
                }
            }
        }
        mViewModel.runTime.observe(this) {
            matchDetail.runTime = it
            updateRunTime()
        }
        //跑马灯广告
        mViewModel.scrollTextList.observe(this) { stl ->
            horseRaceLamp()

        }
        //固定广告
        mViewModel.showAd.observe(this) { ad ->
            ad.data.notNull({ bean ->
                mDatabind.ivShowAd.visibleOrGone(true)
                loadImage(this, ad.data?.imgUrl, mDatabind.ivShowAd, R.drawable.ic_ad_def)
                mDatabind.ivShowAd.setOnClickListener {
                    jumpOutUrl(bean.targetUrl)
                }
            })
        }
        //主播详情接口返回监听处理
        mViewModel.anchor.observe(this) {
            if (it != null) {
                anchor?.apply {
                    userId = it.id ?: ""
                    nickName = it.nickName ?: ""
                    userLogo = it.head ?: ""
                }
                this.anchorId = it.id
                setFocusUI(it.focus)
                setAnchorUI()
            }
        }
        mViewModel.isfocus.observe(this) {
            if (it) {
                appViewModel.updateSomeData.postValue("friends")
                setFocusUI(true)
                /* anchor?.hotValue = anchor?.hotValue?.toInt()?.plus(1).toString()
                 mDatabind.tvDetailTabAnchorFans.text = anchor?.hotValue+"热度值" //主播粉丝数量+1*/
            }
        }
        mViewModel.isUnFocus.observe(this) {
            if (it) {
                appViewModel.updateSomeData.postValue("friends")
                setFocusUI(false)
                /* anchor?.hotValue = anchor?.hotValue?.toInt()?.minus(1).toString()
                 mDatabind.tvDetailTabAnchorFans.text = anchor?.hotValue+"热度值" //主播粉丝数量-1*/
            }
        }

        /* appViewModel.appPolling.observe(this) {
             try {
                 //防止数据未初始化的情况
                 if (::matchDetail.isInitialized && matchDetail.status in 0..if (matchType == "1") 7 else 9) {
                     // mViewModel.getMatchDetail(matchId, matchType)
                 }
             } catch (_: Exception) {}
         }*/
    }

    /**
     * 设置跑马灯
     */
    fun  horseRaceLamp(){
        if( mViewModel.scrollTextList.value!=null){
         var stl= mViewModel.scrollTextList.value
            mDatabind.marqueeView.visibleOrInvisible(stl!!.isSuccess && stl.data!!.size > 0)
            stl!!.data.notNull({ list ->
                if (list.size > 0) {
                    //滚动条广告
                    mDatabind.marqueeView.isSelected = true
                    //随机取一个数 val random = (0 until list.size).random()
                    val random = (0..list.size).random() % list.size
                    mDatabind.marqueeView.text = list[random].name    /*+"                                                                                             "*/
                    mDatabind.marqueeView.setOnClickListener {
                        jumpOutUrl(list[random].targetUrl)
                    }
                }
            }, {})
        }

    }

    private var focus: Boolean = false
    private fun setFocusUI(focus: Boolean) {
        this.focus = focus
        if (this.focus) {
            mDatabind.tvTabAnchorFollow.text = getString(R.string.dis_focus)
            mDatabind.tvTabAnchorFollow.setTextColor(getColor(R.color.c_94999f))
            mDatabind.tvTabAnchorFollow.setBackgroundResource(R.drawable.selector_focused_r20)
        } else {
            mDatabind.tvTabAnchorFollow.text = getString(R.string.add_focus)
            mDatabind.tvTabAnchorFollow.setTextColor(getColor(R.color.c_34a853))
            mDatabind.tvTabAnchorFollow.setBackgroundResource(R.drawable.selector_focus_r20)
        }
    }

    /**
     * 根据状态更新比赛运行时间
     */
    private fun updateRunTime() {
        if (matchDetail.status in 2..if (matchType == "1") 7 else 9) {
            if (matchType == "1" && matchDetail.status == 3) {
                //中场特殊处理
                mDatabind.tvMatchTime.text =
                    TimeUtil.timeStamp2Date(matchDetail.matchTime.toLong(), "MM-dd HH:mm")
                mDatabind.tvMatchTimeS.visibleOrGone(false)
            } else {
                setMatchStatusTime(
                    mDatabind.tvMatchTime,
                    mDatabind.tvMatchTimeS,
                    matchDetail.matchType,
                    matchDetail.status,
                    matchDetail.runTime
                )
            }
        } else {
            mDatabind.tvMatchTime.text =
                TimeUtil.timeStamp2Date(matchDetail.matchTime.toLong(), "MM-dd HH:mm")
            mDatabind.tvMatchTimeS.visibleOrGone(false)
        }
    }

    private fun changeUI() {
        showHideLive()
        setAnchorUI()
        if (matchType == "1") {
            mDatabind.toolbar.setBackgroundResource(if (isHasAnchor) R.color.translet else R.drawable.bg_top_football)
        } else {
            mDatabind.toolbar.setBackgroundResource(if (isHasAnchor) R.color.translet else R.drawable.bg_top_basketball)
        }
        //设置Tab
        setNewViewPager(
            mTitles,
            mFragList,
            isHasAnchor,
            anchor?.userId,
            matchDetail,
            pager2Adapter,
            mDatabind.viewPager,
            mDatabind.magicIndicator,this
        )
        //有主播
        if (isHasAnchor) {
            mDatabind.viewPager.postDelayed({
                if (CacheUtil.isLogin()) {
                    mViewModel.addLiveHistory(anchor?.liveId)
                }

            }, 200)
        }
        //更新聊天室
        mViewModel.anchorInfo.value = anchor
    }

    private fun setAnchorUI() {
        mDatabind.cslAnchor.visibleOrGone(isHasAnchor)
//        GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
//            delay(500L) // 延迟1秒（1000毫秒）
//            mDatabind.cslAnchor.visibleOrGone(isHasAnchor)
//
//        }
        mDatabind.tvDetailTabAnchorFans.text = anchor?.hotValue + "${resources.getString(R.string.live_txt_heat)}" //热度
        mDatabind.tvTabAnchorNick.text = anchor?.nickName  //主播昵称
        mViewModel.anchorName = anchor?.nickName ?: ""
        loadImage(
            this,
            anchor?.userLogo,
            mDatabind.ivTabAnchorAvatar,
            R.drawable.default_anchor_icon
        ) //主播头像
        //点击私信跳转聊天界面逻辑，根据传参来跳转
        mDatabind.tvTabAnchorChat.setOnClickListener { v ->
            appViewModel.emptychatUserEvent.postValue(anchor?.userId)
            judgeLogin {
                startNewActivity<ChatActivity>() {
                    putExtra(Constants.USER_ID, anchor?.userId)
                    putExtra(Constants.USER_NICK, anchor?.nickName)
                    putExtra(Constants.USER_HEAD, anchor?.userLogo)
                }
            }
        }
        //点击关注或者取消关注
        mDatabind.tvTabAnchorFollow.setOnClickListener {
            judgeLogin {
                if (!focus) {
                    mViewModel.followAnchor(anchorId ?: "")
                } else {

                    //取消关注
                    cancellationDialog(this){
                        if (it) {//点击了确定
                            mViewModel.unFollowAnchor(anchorId ?: "")
                        }
                    }


                }
            }
        }
    }


    private fun getAnchor(action: (String?) -> Unit = {}) {
        matchDetail.anchorList.notNull({ list ->
            "anchorList===${Gson().toJson(list)}".loge()
            //降序 sortByDescending可变列表的排序； sortedBytDescending 不可变列表的排序，需创建一个新的列表来保存排序后的结果
            list.sortByDescending {
                it.hotValue
            }
            // 是否找到流
            var findAnchor = false
            if (isHasAnchor) {
                for ((i, item) in list.withIndex()) {
                    if (anchorId == item.userId) {
                        isShowVideo = true
                        item.isSelect = true
                        anchor = item
                        action.invoke(item.playUrl)
                        findAnchor = true
                        break
                    }
                }
            }
            //没找到主播流 播第一个主播
            if (!findAnchor) {
                val item = list[0]
                item.isSelect = true
                anchor = item
                if (item.pureFlow) {//纯净流 无主播
                    isHasAnchor = false
                    if (item.playUrl.isNullOrEmpty()) {
                        isShowVideo = false
                    } else {
                        isShowVideo = true
                        action.invoke(item.playUrl)
                    }
                } else {
                    isShowVideo = true
                    isHasAnchor = true
                    action.invoke(item.playUrl)
                }
            }
        })
    }

    private fun jumpOutUrl(url: String) {
        if (url.contains("http")) {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl: Uri = Uri.parse(url)
            intent.data = contentUrl
            startActivity(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        topActivity = false
    }

    override fun onResume() {
        super.onResume()
        topActivity = true

        GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
            delay(1000L) // 延迟1秒（1000毫秒）
            // 在这里写下你想要在1秒后执行的代码
            initScreenProjection()

            ClingDLNAManager.getInstant().getSearchDevices().observe(this@MatchDetailActivity) {
//            mBinding.toolbar.subtitle = Utils.getWiFiIpAddress(this)

                Utils.getWiFiIpAddress(this@MatchDetailActivity)
                if (popup != null && !popup!!.isDismiss) {
//                deviceList.clear()
//                deviceList.addAll(it)
                    var list = ArrayList<ClingDevice>()
                    list.addAll(it)
                    popup!!.setDate(list)
                }


            }
        }

        if (isShowVideo && !isTopActivity(this)) {
            startVideo(anchor?.playUrl)
        }
    }


    override fun onDestroy() {
        //关闭投屏服务之类的
        ClingDLNAManager.stopLocalFileService(this)
        stopUpnpService(mUpnpServiceConnection)
        ClingDLNAManager.getInstant().destroy()

        super.onDestroy()
        MyWsManager.getInstance(App.app)?.removeLiveStatusListener(this.toString())
        MyWsManager.getInstance(App.app)?.removeOtherPushListener(this.toString())
    }

    override val gSYVideoPlayer: StandardGSYVideoPlayer
        get() = mDatabind.videoPlayer
    override val gSYVideoOptionBuilder: GSYVideoOptionBuilder
        get() = GSYVideoOptionBuilder()
            .setStartAfterPrepared(true)
            .setCacheWithPlay(true)//是否启用缓存，直播就不用
            .setVideoTitle("视频")
            .setIsTouchWiget(true) //.setAutoFullWithSize(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setShowFullAnimation(false) //打开动画
            .setNeedLockFull(true)
            .setNeedOrientationUtils(false)
            .setSeekRatio(1f)

    override fun clickForFullScreen() {
        //Log.e("TAG", "clickForFullScreen: ===")
    }
    //这个横竖屏回调了以后，在执行播放器里面的横竖屏的方法，所以横屏可以获取到数据
    override fun screenStatus(status: Boolean) {

        mDatabind.videoPlayer.setFullScreenCover(matchType)
        mDatabind.videoPlayer.broadcasting(isShowVideo, status)

//        mDatabind.videoPlayer.currentPlayer.
    }


//    override fun screenState() {
//      mDatabind.videoPlayer.getScreenState()
//    }

    override fun onPlayError(url: String, vararg objects: Any) {
        "video error=====================".loge("====")
        //showHideLive(true)

        mDatabind.apply {

            if (isShowVideo) {
                //有视频布局  修改
                rltVideo.visibleOrGone(false)
                lltLiveError.visibleOrGone(true)
                tvReload.setOnClickListener {
                    startVideo(anchor?.playUrl)
                    showHideLive()
                }
            }
        }
        //如果是横屏的时候报错就竖屏
        if (mDatabind.videoPlayer.isIfCurrentIsFullscreen) {
            exitFullScreen()
        }
    }

    /**
     * 退出全屏
     */
    override fun onQuitFullscreen(url: String, vararg objects: Any) {
        super.onQuitFullscreen(url, *objects)
        horseRaceLamp()
    }

    override fun onStartPrepared(url: String, vararg objects: Any) {
        super.onStartPrepared(url, *objects)

    }

    override val detailOrientationRotateAuto: Boolean
        get() = false


    /**
     * 初始化投屏
     */
    private fun initScreenProjection() {
        bindServices()
        ClingDLNAManager.startLocalFileService(this)
    }

    private fun bindServices() { // Bind UPnP service
        mUpnpServiceConnection = startBindUpnpService {
            Log.i("Cling", "startBindUpnpService OK")
        }
    }

    /**
     * Add control observers
     * 监听control的状态
     */
    private fun DeviceControl.addControlObservers() {
//        PLAYING   播放
//         NO_MEDIA_PRESENT    没有
//         PAUSED_PLAYBACK    暂停
//        STOPPED    关闭

        getCurrentState().observe(this@MatchDetailActivity) {
//            mBinding.playState.text = "当前状态：$it"
            Log.i("DDDDD", "1111===" + it.toJson())
//            if(it.value.isNotEmpty()&&it.value.equals("STOPPED")){
//                control?.stop(object : ServiceActionCallback<Unit> {
//                    override fun onSuccess(result: Unit) {
////                        "停止成功".showToast()
//                        Log.i("VVVVVVVVVVV","停止成功")
//                    }
//
//                    override fun onFailure(msg: String) {
////                        "停止失败".showToast()
//                        Log.i("VVVVVVVVVVV","停止失败")
//                    }
//                })
//            }


        }
        getCurrentPositionInfo().observe(this@MatchDetailActivity) {
//            mBinding.playPosition.text = "当前进度：${it.toJson()}"
            Log.i("SSSSSSSSSSSS", "2222222222222222===" + it.toJson())

        }
        getCurrentVolume().observe(this@MatchDetailActivity) {
//            mBinding.playVolume.text = "当前音量：$it"
            Log.i("SSSSSSSSSSSS", "33333333333===" + it)
        }
        getCurrentMute().observe(this@MatchDetailActivity) {
//            mBinding.playVolume.text = "当前静音：$it"
            Log.i("SSSSSSSSSSSS", "4444444444444===" + it)
        }
    }


}