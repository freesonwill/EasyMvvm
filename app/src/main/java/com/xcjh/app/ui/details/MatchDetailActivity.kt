package com.xcjh.app.ui.details

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ImmersionBar.getStatusBarHeight
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.push.HmsMessaging.DEFAULT_TOKEN_SCOPE
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
import com.xcjh.app.ui.MainActivity
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.ui.details.common.GSYBaseActivity
import com.xcjh.app.ui.details.fragment.*
import com.xcjh.app.utils.*
import com.xcjh.app.view.balldetail.ControlShowListener
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.listener.LiveStatusListener
import com.xcjh.app.websocket.listener.OtherPushListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.*
import com.xcjh.base_lib.utils.view.textString
import com.xcjh.base_lib.utils.view.visibleOrGone
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

    // private var playUrl: String? = "rtmp://liteavapp.qcloud.com/live/liteavdemoplayerstreamid"
    // private var playUrl: String? = "https://sf1-hscdn-tos.pstatp.com/obj/media-fe/xgplayer_doc_video/flv/xgplayer-demo-720p.flv"
    private var playUrl: String? = ""

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this).statusBarDarkFont(false)//黑色
            .titleBarMarginTop(mDatabind.rltTop).init()
        Thread() {
            try {
                //HmsInstanceId.getInstance(this).getToken("109888465",DEFAULT_TOKEN_SCOPE).loge("push====token===")
                //FirebaseInstanceId.getInstance().getInstanceId()
            } catch (e: Exception) {
                "token failed! Catch exception : $e".loge("push====token===")
            }
            try {

                Firebase.messaging.token.addOnCompleteListener {
                    OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            "Fetching FCM registration token failed".loge("push====token===")
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result
                        token.loge("push====token===")

                    }
                }
            } catch (e: Exception) {
                "token failed! Catch exception : $e".loge("push====token===")
            }

        }.start()

        //解决toolbar左边距问题
        mDatabind.toolbar.setContentInsetsAbsolute(0, 0)
        mDatabind.viewTopBg.layoutParams.height = getStatusBarHeight(this)
        mViewModel.tt = 5
        intent.extras?.apply {
            matchType = getString("matchType", "1")
            matchId = getString("matchId", "0")
            matchName = getString("matchName", "")
            anchorId = getString("anchorId", null)
            //  playUrl = getString("videoUrl", null)
            isHasAnchor = !anchorId.isNullOrEmpty()
            setData()
        }
        initStaticUI()
        initVp()
        initOther()
        // setTestTab()
    }

    private fun setData() {
        mViewModel.getMatchDetail(matchId, matchType, true)
        if (isHasAnchor){
            mViewModel.getDetailAnchorInfo(anchorId)
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
        mDatabind.apply {
            if (isClose) {
                rltVideo.visibleOrGone(false)
                cslMatchInfo.visibleOrGone(false)
                lltNoLive.visibleOrGone(true)
                topLiveTitle.visibleOrGone(true)
                topNoLiveTitle.visibleOrGone(false)
                lltLiveError.visibleOrGone(false)
            } else {
                lltNoLive.visibleOrGone(false)
                //有视频布局
                rltVideo.visibleOrGone(isShowVideo)
                topLiveTitle.visibleOrGone(isShowVideo)
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
                    if (anchor?.liveId == bean.id && anchor?.liveId == bean.id) {
                        isShowVideo = true
                        showHideLive()
                        if (isTopActivity(this@MatchDetailActivity) && !isPause) {
                            startVideo(anchor?.playUrl)
                        }
                    }
                }

                override fun onCloseLive(bean: LiveStatus) {
                    //"onReceive========${bean.id}===${anchor?.liveId}".loge()
                    if (anchor?.liveId == bean.id && anchor?.liveId == bean.id) {
                        mDatabind.videoPlayer.release()
                        isShowVideo = false
                        showHideLive(true)
                        matchDetail.anchorList?.forEach { it ->
                            if (it.liveId == anchor?.liveId) {
                                matchDetail.anchorList?.remove(it)
                            }
                        }
                    }
                }

                override fun onChangeLive(bean: LiveStatus) {
                    if (isShowVideo) {
                        if (anchor?.liveId == bean.id && anchor?.liveId == bean.id) {
                            anchor?.playUrl = bean.playUrl
                            showHideLive()
                            if (isTopActivity(this@MatchDetailActivity) && !isPause) {
                                startVideo(anchor?.playUrl)
                            }
                        }
                    } else {
                        if (anchor?.liveId == bean.id && anchor?.liveId == bean.id) {
                            anchor?.playUrl = bean.playUrl
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
                                        //runTime = 123
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

    private fun setBaseListener() {

        //分享按钮
        mDatabind.tvToShare.setOnClickListener {
            //分享 固定地址
            //复制链接成功
            val url = if (isHasAnchor) {
                ApiComService.SHARE_IP + "#/roomDetail?id=${matchId}&liveId=${anchor?.liveId}&type=${matchType}&userId=${anchor?.userId}"
            } else {
                ApiComService.SHARE_IP + "#/roomDetail?id=${matchId}&type=${matchType}&pureFlow=true"
            }
          /*  copyToClipboard(url)
            myToast(getString(R.string.copy_success))*/
            shareText(this,url)
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
                //切换主播
                this.anchor = anchor
                if (anchor.pureFlow) {
                    isHasAnchor = false
                    isShowVideo = !anchor.playUrl.isNullOrEmpty()
                } else {
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
                //更新聊天室
                mViewModel.anchorInfo.value = anchor
            }
        } else {
            myToast("no data")
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
            mDatabind.tvAwayName.text = "${matchDetail.homeName}\n(主)"
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.def_basketball)
                .into(mDatabind.ivAwayIcon)
            //客队名称以及图标
            mDatabind.tvHomeName.text = matchDetail.awayName
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.def_basketball)
                .into(mDatabind.ivHomeIcon)
        }
        //赛事名字和比赛时间
        mDatabind.tvCompetitionName.text = matchDetail.competitionName
        mDatabind.tvMatchTime.text =
            TimeUtil.timeStamp2Date(matchDetail.matchTime.toLong(), "yyyy-MM-dd HH:mm")
        needWsToUpdateUI()
    }

    /**
     * 需要实时更新的UI
     */
    private fun needWsToUpdateUI() {
        matchName = if (matchDetail.status in 2..if (matchType == "1") 8 else 10) {
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
                    matchDetail.homeScore.toString() + " : " + matchDetail.awayScore.toString()
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
                    matchDetail.awayScore.toString() + " : " + matchDetail.homeScore.toString()
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
        mDatabind.videoPlayer.setUp(url, false, "")
        mDatabind.videoPlayer.startPlayLogic()
        mDatabind.videoPlayer.setGSYStateUiListener {
            //it.toString().loge("======")
            if (it == 2) {
                // GSYVideoType.setScreenScaleRatio(mDatabind.videoPlayer.gsyVideoManager.currentVideoWidth / mDatabind.videoPlayer.gsyVideoManager.currentVideoHeight.toFloat())
                GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
            }
        }
        mDatabind.videoPlayer.setControlListener(object : ControlShowListener {
            override fun onShow() {
                if (isShowVideo) {
                    mDatabind.topLiveTitle.visibleOrGone(true)
                    mDatabind.tvToShare.visibleOrGone(true)
                    mDatabind.tvSignal.visibleOrGone(true)
                }
            }

            override fun onHide() {
                if (isShowVideo) {
                    mDatabind.topLiveTitle.visibleOrGone(false)
                    mDatabind.tvToShare.visibleOrGone(false)
                    mDatabind.tvSignal.visibleOrGone(false)
                }
            }
        })
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
                        startVideo(it)
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
            mDatabind.marqueeView.visibleOrGone(stl.isSuccess && stl.data!!.size > 0)
            stl.data.notNull({ list ->
                //滚动条广告
                mDatabind.marqueeView.isSelected = true
                val random = (0..list.size).random() % list.size
                mDatabind.marqueeView.text =
                    list[random].name    /*+"                                                                                             "*/
                mDatabind.marqueeView.setOnClickListener {
                    jumpOutUrl(list[random].targetUrl)
                }
            }, {})
        }
        //固定广告
        mViewModel.showAd.observe(this) { ad ->
            ad.data.notNull({ bean ->
                loadImage(this,bean.imgUrl,mDatabind.ivShowAd, com.xcjh.base_lib.R.drawable.ic_default_bg)
                mDatabind.ivShowAd.setOnClickListener {
                    jumpOutUrl(bean.targetUrl)
                }

            })
        }
        //主播详情接口返回监听处理
        mViewModel.anchor.observe(this) {
            if (it != null) {
                this.anchorId = it.id
                setFocusUI(it.focus)
            }
        }
        mViewModel.isfocus.observe(this) {
            if (it) {
                appViewModel.updateSomeData.postValue("friends")
                setFocusUI(true)
                anchor?.hotValue= anchor?.hotValue?.toInt()?.plus(1).toString()
                mDatabind.tvDetailTabAnchorFans.text =  anchor?.hotValue+"热度值" //主播粉丝数量+1
            }
        }
        mViewModel.isUnFocus.observe(this) {
            if (it) {
                appViewModel.updateSomeData.postValue("friends")
                setFocusUI(false)
                anchor?.hotValue= anchor?.hotValue?.toInt()?.minus(1).toString()
                mDatabind.tvDetailTabAnchorFans.text =
                    anchor?.hotValue+"热度值" //主播粉丝数量-1
            }
        }
        /* appViewModel.appPolling.observe(this) {
             try {
                 //防止数据未初始化的情况
                 if (::matchDetail.isInitialized && matchDetail.status in 0..if (matchType == "1") 7 else 9) {
                     // mViewModel.getMatchDetail(matchId, matchType)
                 }
             } catch (_: Exception) {
             }

         }*/
    }

    private var focus: Boolean = false
    private fun setFocusUI(focus: Boolean) {
        this.focus = focus
        if (this.focus) {
            mDatabind.tvTabAnchorFollow.text = getString(R.string.dis_focus)
            mDatabind.tvTabAnchorFollow.setTextColor(getColor(R.color.c_94999f))
            mDatabind.tvTabAnchorFollow.setBackgroundResource(R.drawable.shape_red_pressed_r20)
            mDatabind.tvTabAnchorFollow.backgroundTintList= ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.c_1f1f20)
            )
        } else {
            mDatabind.tvTabAnchorFollow.text = getString(R.string.add_focus)
            mDatabind.tvTabAnchorFollow.setTextColor(getColor(R.color.c_34a853))
            mDatabind.tvTabAnchorFollow.setBackgroundResource(R.drawable.shape_line_r20)
            mDatabind.tvTabAnchorFollow.backgroundTintList= null
        }
    }

    /**
     * 根据状态更新比赛运行时间
     */
    private fun updateRunTime() {
        if (matchDetail.status in 2..if (matchType == "1") 7 else 9) {
            setMatchStatusTime(
                mDatabind.tvMatchTime,
                mDatabind.tvMatchTimeS,
                matchDetail.matchType,
                matchDetail.status,
                matchDetail.runTime
            )
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
        setNewViewPager(
            mTitles,
            mFragList,
            isHasAnchor,
            anchor?.userId,
            matchDetail,
            pager2Adapter,
            mDatabind.viewPager,
            mDatabind.magicIndicator
        )
        //有主播
        if (isHasAnchor) {
            mDatabind.viewPager.postDelayed(
                {
                    if (CacheUtil.isLogin()) {
                        mViewModel.addLiveHistory(anchor?.liveId)
                    }
                    mViewModel.getDetailAnchorInfo(anchor?.userId)
                },
                200
            )
        }

    }

    private fun setAnchorUI() {
        mDatabind.cslAnchor.visibleOrGone(isHasAnchor)
        mDatabind.tvDetailTabAnchorFans.text = anchor?.hotValue+"热度值" //热度
        mDatabind.tvTabAnchorNick.text = anchor?.nickName  //主播昵称
        loadImage(this,anchor?.userLogo,mDatabind.ivTabAnchorAvatar,R.drawable.default_anchor_icon) //主播头像
        //点击私信跳转聊天界面逻辑，根据传参来跳转
        mDatabind.tvTabAnchorChat.setOnClickListener { v ->
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
                    mViewModel.unFollowAnchor(anchorId ?: "")
                }
            }
        }
    }


    private fun getAnchor( action: (String?) -> Unit = {}) {
        matchDetail.anchorList.notNull({ list ->
            "anchorList===${Gson().toJson(list)}".loge()
            //降序 sortByDescending可变列表的排序； sortedBytDescending 不可变列表的排序，需创建一个新的列表来保存排序后的结果
           list.sortByDescending  {
                it.hotValue
            }
            // 是否找到流
            var findAnchor = false
            for ((i, item) in list.withIndex()) {
                if (isHasAnchor) {
                    if (anchorId == item.userId) {
                        isShowVideo = true
                        item.isSelect =true
                        anchor = item
                        action.invoke(item.playUrl)
                        findAnchor = true
                        break
                    }
                } else {
                    //默认展示第一条主播
                    if (i == 0) {
                        findAnchor = true
                        item.isSelect = true
                        anchor = item
                        if (item.pureFlow) {//纯净流 无主播 (目前无视频，以后可能有)
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
                        break
                    }
                }
            }
            //没找到主播流
            if (!findAnchor){
                for ((i, item) in list.withIndex()) {
                    if (i == 0) {
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
                        break
                    }
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

    override fun onResume() {
        super.onResume()
        if (isShowVideo && !isTopActivity(this)) {
            startVideo(anchor?.playUrl)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyWsManager.getInstance(App.app)?.removeLiveStatusListener(this.toString())
        MyWsManager.getInstance(App.app)?.removeOtherPushListener(this.toString())
    }

    override val gSYVideoPlayer: StandardGSYVideoPlayer
        get() = mDatabind.videoPlayer
    override val gSYVideoOptionBuilder: GSYVideoOptionBuilder
        get() = GSYVideoOptionBuilder()
            .setLooping(true)
            .setStartAfterPrepared(true)
            .setCacheWithPlay(true)
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
    override fun onPlayError(url: String, vararg objects: Any) {
        "video error=====================".loge("====")
        //showHideLive(true)
        mDatabind.apply {
            if (isShowVideo){
                //有视频布局
                rltVideo.visibleOrGone(false)
                lltLiveError.visibleOrGone(true)
                tvReload.setOnClickListener {
                    startVideo(anchor?.playUrl)
                    showHideLive()
                }
            }
        }
    }
    override val detailOrientationRotateAuto: Boolean
        get() = false

}