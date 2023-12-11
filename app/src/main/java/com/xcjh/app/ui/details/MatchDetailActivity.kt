package com.xcjh.app.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ImmersionBar.getStatusBarHeight
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.xcjh.app.R
import com.xcjh.app.adapter.ViewPager2Adapter
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.AnchorListBean
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.bean.TabBean
import com.xcjh.app.databinding.ActivityMatchDetailBinding
import com.xcjh.app.isTopActivity
import com.xcjh.app.net.ApiComService
import com.xcjh.app.ui.chat.ChatActivity
import com.xcjh.app.ui.details.common.GSYBaseActivity
import com.xcjh.app.ui.details.fragment.*
import com.xcjh.app.utils.*
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
import kotlin.collections.ArrayList
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
    private var signalPos: Int = 0 //当前选择的信号源pos

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
            .titleBar(mDatabind.rltTop).init()
        //解决toolbar左边距问题
        mDatabind.toolbar.setContentInsetsAbsolute(0, 0)
        mDatabind.viewTopBg.layoutParams.height= getStatusBarHeight(this)
        mViewModel.tt = 5
        intent.extras?.apply {
            matchType = getString("matchType", "1")
            matchId = getString("matchId", "0")
            matchName = getString("matchName", "")
            anchorId = getString("anchorId", null)
            //  playUrl = getString("videoUrl", null)
            isHasAnchor = !anchorId.isNullOrEmpty()
            //mDatabind.lltSignal.visibleOrGone(isHasAnchor)
            setData()
        }
        initStaticUI()
        initVp()
        initOther()
        // setTestTab()
    }

    private fun setData() {
        mViewModel.getMatchDetail(matchId, matchType, true)
        mViewModel.getScrollTextList()
        mViewModel.getShowAd()
    }

    private fun initStaticUI() {
        //进入界面需要传：比赛类型(1：足球；2：篮球)、比赛ID、比赛名称、主队客队名称头像、比赛时间、状态、公告、在线视频，后面改成传bean
        //比赛名称
        mDatabind.tvTitle.text = matchName ?: ""
        //比赛类型
        if (matchType == "1") {
            mDatabind.ivMatchBg.setBackgroundResource(R.drawable.bg_status_football)
            mDatabind.toolbar.setBackgroundResource(if (isHasAnchor)R.color.translet else R.drawable.bg_top_football)
        } else {
            mDatabind.ivMatchBg.setBackgroundResource(R.drawable.bg_status_basketball)
            mDatabind.toolbar.setBackgroundResource(if (isHasAnchor)R.color.translet else R.drawable.bg_top_basketball)
        }
        startTimeAnimator(mDatabind.tvTopMatchStatusTimeS)
        startTimeAnimator(mDatabind.tvMatchStatusTimeS)
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
                mDatabind.lltMatchTitle.alpha = 1 - v
                //折叠后显示top
                mDatabind.cslTopMatchStatus.alpha = v
                mDatabind.toolbar.background.alpha = (v * 255).toInt()
                mDatabind.lltSignal.isClickable = !(v < 2 && v > 0.8)
            }
        })
        initVideoBuilderMode()
        MyWsManager.getInstance(App.app)
            ?.setLiveStatusListener(this.toString(), object : LiveStatusListener {
                override fun onOpenLive(bean: LiveStatus) {
                    if (anchor?.liveId == bean.id && matchId == bean.matchId) {
                        isShowVideo = true
                        mDatabind.ivNoLive.visibleOrGone(false)
                        mDatabind.videoPlayer.visibleOrGone(true)
                        mDatabind.cslMatchStatus.visibleOrGone(false)
                        if (isTopActivity(this@MatchDetailActivity) && !isPause) {
                            startVideo(anchor?.playUrl)
                        }
                    }
                }

                override fun onCloseLive(bean: LiveStatus) {
                    if (anchor?.liveId == bean.id && matchId == bean.matchId) {
                        mDatabind.videoPlayer.release()
                        isShowVideo = false
                        mDatabind.ivNoLive.visibleOrGone(true)
                        mDatabind.videoPlayer.visibleOrGone(false)
                        mDatabind.cslMatchStatus.visibleOrGone(false)
                    }
                }

                override fun onChangeLive(bean: LiveStatus) {
                    if (isShowVideo) {
                        if (anchor?.liveId == bean.id && matchId == bean.matchId) {
                            anchor?.playUrl = bean.playUrl
                            mDatabind.ivNoLive.visibleOrGone(false)
                            mDatabind.videoPlayer.visibleOrGone(true)
                            mDatabind.cslMatchStatus.visibleOrGone(false)
                            if (isTopActivity(this@MatchDetailActivity) && !isPause) {
                                startVideo(anchor?.playUrl)
                            }
                        }
                    } else {
                        if (anchor?.liveId == bean.id && matchId == bean.matchId) {
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
            var url = if (isHasAnchor) {
                ApiComService.SHARE_IP + "#/roomDetail?id=${matchId}&liveId=${anchor?.liveId}&type=${matchType}&userId=${anchor?.userId}"
            } else {
                ApiComService.SHARE_IP + "#/roomDetail?id=${matchId}&type=${matchType}&pureFlow=true"
            }

            copyToClipboard(url)
            myToast(getString(R.string.copy_success))
            //jumpOut(ad.data!![0].targetUrl)
        }
        //信号源
        mDatabind.lltSignal.setOnClickListener {
            //
            if (matchDetail.anchorList?.isNotEmpty() == true) {
                showSignalDialog(matchDetail.anchorList!!, signalPos) { anchor, pos ->
                    signalPos = pos
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
    }

    /**
     * 设置基础UI
     */
    private fun showBaseUI() {
        matchName = matchDetail.competitionName + "  " +
                if (matchType == "1") {
                    "${matchDetail.homeName ?: ""} VS ${matchDetail.awayName ?: ""}"
                } else "${matchDetail.awayName ?: ""} VS ${matchDetail.homeName ?: ""}"
        mDatabind.tvTitle.text = matchName
        if (matchType == "1") {//足球
            //主队名称以及图标
            mDatabind.tvHomeName.text = matchDetail.homeName ?: ""
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.default_team_logo)
                .into(mDatabind.ivHomeIcon)
            //客队名称以及图标
            mDatabind.tvAwayName.text = matchDetail.awayName
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.default_team_logo)
                .into(mDatabind.ivAwayIcon)
        } else {
            //主队名称以及图标
            mDatabind.tvAwayName.text = "${matchDetail.homeName}\n(主)"
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.default_team_logo)
                .into(mDatabind.ivAwayIcon)
            //客队名称以及图标
            mDatabind.tvHomeName.text = matchDetail.awayName
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.default_team_logo)
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
        //上滑停靠栏
        getMatchStatus(mDatabind.tvTopMatchStatus, matchDetail.matchType, matchDetail.status)
        //比赛状态
        getMatchStatus(mDatabind.tvMatchStatus, matchDetail.matchType, matchDetail.status)
        updateRunTime()
        //有比分的情况 足球status正在比赛是[2,8] 篮球是[2,10]
        if (matchType == "1") {
            //足球
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.default_team_logo)
                .into(mDatabind.ivTopHomeIcon)
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.default_team_logo)
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
            Glide.with(this).load(matchDetail.awayLogo).placeholder(R.drawable.default_team_logo)
                .into(mDatabind.ivTopHomeIcon)
            Glide.with(this).load(matchDetail.homeLogo).placeholder(R.drawable.default_team_logo)
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
        mDatabind.videoPlayer.setUp(url)
        mDatabind.videoPlayer.startPlayLogic()
        mDatabind.videoPlayer.setGSYStateUiListener {
            //it.toString().loge("======")
            if (it == 2) {
                // GSYVideoType.setScreenScaleRatio(mDatabind.videoPlayer.gsyVideoManager.currentVideoWidth / mDatabind.videoPlayer.gsyVideoManager.currentVideoHeight.toFloat())
                GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
            }
        }
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
                    mViewModel.startTimeRepeat(match.runTime)
                    ///判断当前是否展示直播
                    getAnchor(match) {
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
                mDatabind.marqueeView.text = list[random].name    /*+"                                                                                             "*/
                mDatabind.marqueeView.setOnClickListener {
                    jumpOutUrl(list[random].targetUrl)
                }
            }, {})
        }
        //固定广告
        mViewModel.showAd.observe(this) { ad ->
            ad.data.notNull({ bean ->
                Glide.with(this).load(bean.imgUrl)
                    .placeholder(com.xcjh.base_lib.R.drawable.ic_default_bg)
                    .into(mDatabind.ivShowAd)
                mDatabind.ivShowAd.setOnClickListener {
                    jumpOutUrl(bean.targetUrl)
                }

            })
        }
        //主播详情接口返回监听处理
        mViewModel.anchor.observe(this) {
            if (it != null) {
                this.anchorId = it.id
                mDatabind.tvTabAnchorNick.text = it.nickName  //主播昵称
                mDatabind.tvDetailTabAnchorFans.text = it.fansCount //主播粉丝数量

                setFocusUI(it.focus)
                Glide.with(this).load(it.head).placeholder(mDatabind.ivTabAnchorAvatar.drawable)
                    .into(mDatabind.ivTabAnchorAvatar) //主播头像
                //点击私信跳转聊天界面逻辑，根据传参来跳转
                mDatabind.tvTabAnchorChat.setOnClickListener { v ->
                    judgeLogin {
                        startNewActivity<ChatActivity>() {
                            putExtra(Constants.USER_ID, it.id)
                            putExtra(Constants.USER_NICK, it.nickName)
                            putExtra(Constants.USER_HEAD, it.head)
                        }
                    }
                }
                //点击关注或者取消关注
                mDatabind.tvTabAnchorFollow.setOnClickListener {
                    judgeLogin {
                        if (!focus) {
                            mViewModel.followAnchor(anchorId?:"")
                        } else {
                            mViewModel.unFollowAnchor(anchorId?:"")
                        }
                    }
                }
            }
        }
        mViewModel.isfocus.observe(this) {
            if (it) {
                appViewModel.updateSomeData.postValue("friends")
                setFocusUI(true)
                mDatabind.tvDetailTabAnchorFans.text =
                    (mDatabind.tvDetailTabAnchorFans.textString().toInt() + 1).toString() //主播粉丝数量+1
            }
        }
        mViewModel.isUnFocus.observe(this) {
            if (it) {
                appViewModel.updateSomeData.postValue("friends")
                setFocusUI(false)
                mDatabind.tvDetailTabAnchorFans.text =
                    (mDatabind.tvDetailTabAnchorFans.textString().toInt() - 1).toString() //主播粉丝数量-1
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
        } else {
            mDatabind.tvTabAnchorFollow.text = getString(R.string.add_focus)
        }
    }
    /**
     * 根据状态更新比赛运行时间
     */
    private fun updateRunTime() {
        setMatchStatusTime(
            mDatabind.tvTopMatchStatusTime,
            mDatabind.tvTopMatchStatusTimeS,
            matchDetail.matchType,
            matchDetail.status,
            matchDetail.runTime
        )
        setMatchStatusTime(
            mDatabind.tvMatchStatusTime,
            mDatabind.tvMatchStatusTimeS,
            matchDetail.matchType,
            matchDetail.status,
            matchDetail.runTime
        )
    }

    private fun changeUI() {
        mDatabind.ivNoLive.visibleOrGone(false)
        mDatabind.videoPlayer.visibleOrGone(isShowVideo)
        mDatabind.cslMatchStatus.visibleOrGone(!isShowVideo)
        mDatabind.cslAnchor.visibleOrGone(isHasAnchor)
        mDatabind.viewTopBg.visibleOrGone(isHasAnchor)
        if (matchType == "1") {
            mDatabind.toolbar.setBackgroundResource(if (isHasAnchor)R.color.translet else R.drawable.bg_top_football)
        } else {
            mDatabind.toolbar.setBackgroundResource(if (isHasAnchor)R.color.translet else R.drawable.bg_top_basketball)
        }
        setNewViewPager(
            signalPos,
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

    private fun getAnchor(match: MatchDetailBean, action: (String?) -> Unit = {}) {
        match.anchorList.notNull({ list ->
            for ((i, item) in list.withIndex()) {
                if (isHasAnchor) {
                    if (anchorId == item.userId) {
                        isShowVideo = true
                        signalPos = i
                        anchor = item
                        action.invoke(item.playUrl)
                        return@notNull
                    }
                } else {
                    //默认展示第一条主播
                    if (i == 0) {
                        signalPos = 0
                        if (item.pureFlow) {//纯净流 无主播
                            isHasAnchor = false
                            if (item.playUrl.isNullOrEmpty()) {
                                isShowVideo = false
                            } else {
                                isShowVideo = true
                                action.invoke(playUrl)
                            }
                        } else {
                            isShowVideo = true
                            isHasAnchor = true
                            anchor = item
                            action.invoke(playUrl)
                        }
                        return@notNull
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

    override val detailOrientationRotateAuto: Boolean
        get() = false

}