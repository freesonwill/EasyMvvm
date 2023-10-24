package com.xcjh.app.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
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
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.*
import com.xcjh.base_lib.utils.view.visibleOrGone
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

    private val tabs by lazy {
        arrayListOf(
            TabBean(1, name = resources.getStringArray(R.array.str_football_detail_tab)[0]),
            TabBean(2, name = resources.getStringArray(R.array.str_football_detail_tab)[1]),
            TabBean(3, name = resources.getStringArray(R.array.str_football_detail_tab)[2]),
            TabBean(4, name = resources.getStringArray(R.array.str_football_detail_tab)[3]),
            TabBean(5, name = resources.getStringArray(R.array.str_football_detail_tab)[4]),
            TabBean(6, name = resources.getStringArray(R.array.str_football_detail_tab)[5]),
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this).statusBarDarkFont(false)//黑色
            .titleBar(mDatabind.rltTop).init()
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
        mDatabind.tvTitle.text = matchName
        //比赛类型
        if (matchType == "1") {
            mDatabind.ivMatchBg.setBackgroundResource(R.drawable.bg_status_football)
            mDatabind.rltTop.setBackgroundResource(R.drawable.bg_top_football)
        } else {
            mDatabind.ivMatchBg.setBackgroundResource(R.drawable.bg_status_basketball)
            mDatabind.rltTop.setBackgroundResource(R.drawable.bg_top_basketball)
        }
        startTimeAnimator(mDatabind.tvTopMatchStatusTimeS)
        startTimeAnimator(mDatabind.tvMatchStatusTimeS)
    }

    private fun initVp() {
        mDatabind.viewPager.initChangeActivity(this, mFragList,false)
        pager2Adapter = mDatabind.viewPager.adapter as ViewPager2Adapter
        //初始化Tab控件
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.viewPager,
            mTitles,
            selectSize = 16f,
            unSelectSize = 16f,
            selectColor = R.color.c_F5F5F5,
            normalColor = com.xcjh.base_lib.R.color.c_8a91a0,
            typefaceBold = true,
            scrollEnable = true,
            lineIndicatorColor = R.color.c_F5F5F5,
            margin = 12
        ) {
            if (it == 0) {
                setUnScroll(mDatabind.lltFold)
            } else {
                setScroll(mDatabind.lltFold)
            }
        }
    }

    private fun initOther() {
        ///跑马灯设置
        mDatabind.marqueeView.isSelected = true
        mDatabind.rltTop.background.alpha = 0
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
                mDatabind.rltTop.background.alpha = (v * 255).toInt()
            }
        })
        initVideoBuilderMode()
    }

    /**
     * 设置基础UI
     * （比赛中基本固定不变）
     */
    private fun setBaseMatchUI() {
        matchName =
            matchDetail.competitionName + "  " + matchDetail.homeName + "VS" + matchDetail.awayName
        mDatabind.tvTitle.text = matchName
        //上滑停靠栏
        getMatchStatus(mDatabind.tvTopMatchStatus, matchDetail.matchType, matchDetail.status)
        setMatchStatusTime(mDatabind.tvTopMatchStatusTime,
            mDatabind.tvTopMatchStatusTimeS,
            matchDetail.matchType,
            matchDetail.status,
            matchDetail.runTime)
        setMatchStatusTime(mDatabind.tvMatchStatusTime,
            mDatabind.tvMatchStatusTimeS,
            matchDetail.matchType,
            matchDetail.status,
            matchDetail.runTime)
        //有比分的情况 足球status正在比赛是[2,8] 篮球是[2,10]
        mDatabind.tvTopHomeScore.text =
            if (matchDetail.status in 2..if (matchType == "1") 8 else 10) matchDetail.homeScore.toString() else ""
        mDatabind.tvTopAwayScore.text =
            if (matchDetail.status in 2..if (matchType == "1") 8 else 10) matchDetail.awayScore.toString() else ""

        if (matchDetail.status in 2..if (matchType == "1") 8 else 10) {
            mDatabind.tvMatchVs.textSize = 20f
            mDatabind.tvMatchVs.text =
                matchDetail.homeScore.toString() + " : " + matchDetail.awayScore.toString()
        } else {
            mDatabind.tvMatchVs.textSize = 22f
            mDatabind.tvMatchVs.text = getString(R.string.vs)
        }

        Glide.with(this).load(matchDetail.homeLogo).into(mDatabind.ivTopHomeIcon)
        Glide.with(this).load(matchDetail.awayLogo).into(mDatabind.ivTopAwayIcon)
        //私聊按钮
        mDatabind.tvToChat.setOnClickListener {
            //聊天界面还在开发中，先占位
            if (anchor != null) {
                judgeLogin {
                    startNewActivity<ChatActivity>() {
                        putExtra(Constants.USER_ID, anchor?.userId)
                        putExtra(Constants.USER_NICK, anchor?.nickName)
                        putExtra(Constants.USER_HEAD, anchor?.userLogo)
                    }
                }
            }
        }
        //分享按钮
        mDatabind.tvToShare.setOnClickListener {
            //分享 固定地址
            //复制链接成功
            var url = if (isHasAnchor){
                ApiComService.SHARE_IP +"#/roomDetail?id=${matchId}&liveId=${anchor?.liveId}&type=${matchType}&userId=${anchorId}"
            }else{
                ApiComService.SHARE_IP +"#/roomDetail?id=${matchId}&type=${matchType}"
            }

            copyToClipboard(url)
            myToast(getString(R.string.copy_success))
            //jumpOut(ad.data!![0].targetUrl)
        }
        //信号源
        mDatabind.lltSignal.setOnClickListener {
            //
            if (matchDetail.anchorList?.isNotEmpty() == true) {
                showSignalDialog(matchDetail.anchorList!!,signalPos) { anchor,pos ->
                    signalPos = pos
                    if (anchorId == anchor.userId) {
                        //无改变
                        return@showSignalDialog
                    }
                    //切换主播
                    playUrl = anchor.playUrl
                    anchorId = anchor.userId
                    this.anchor = anchor
                    if (anchor.pureFlow){
                        isHasAnchor = false
                        isShowVideo = !anchor.playUrl.isNullOrEmpty()
                    }else{
                        isHasAnchor = true
                        isShowVideo =true
                    }
                    if (isShowVideo){
                        startVideo(playUrl)
                    }else{
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
     * 无主播流时展示比赛状态
     */
    private fun showMatchStatusUI() {
        //主队名称以及图标
        mDatabind.tvHomeName.text = matchDetail.homeName
        Glide.with(this).load(matchDetail.homeLogo).into(mDatabind.ivHomeIcon)
        //客队名称以及图标
        mDatabind.tvAwayName.text = matchDetail.awayName
        Glide.with(this).load(matchDetail.awayLogo).into(mDatabind.ivAwayIcon)
        //赛事名字和比赛时间
        mDatabind.tvCompetitionName.text = matchDetail.competitionName
        mDatabind.tvMatchTime.text =
            TimeUtil.timeStamp2Date(matchDetail.matchTime.toLong(), "yyyy-MM-dd HH:mm")
        //比赛状态
        getMatchStatus(mDatabind.tvMatchStatus, matchDetail.matchType, matchDetail.status)
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
                setBaseMatchUI()
                showMatchStatusUI()
                if (isNeedInit) {
                    isNeedInit = false
                    ///判断当前是否展示直播
                    getSignal(match){
                        startVideo(it)
                    }
                    changeUI()
                }
            }
        }
        //跑马灯广告
        mViewModel.scrollTextList.observe(this) { stl ->
            mDatabind.marqueeView.visibleOrGone(stl.isSuccess && stl.data!!.size > 0)
            stl.data.notNull({ list ->
                //滚动条广告
                mDatabind.marqueeView.isSelected = true
                val random = (0..list.size).random() % list.size
                Log.e(TAG, "random: ====" + random + Gson().toJson(list[random]))
                mDatabind.marqueeView.text = list[random].name
                mDatabind.marqueeView.setOnClickListener {
                    jumpOutUrl(list[random].targetUrl)
                }
            }, {})
        }
        //固定广告
        mViewModel.showAd.observe(this) { ad ->
            ad.data.notNull({ bean ->
                Glide.with(this).load(bean.imgUrl).into(mDatabind.ivShowAd)
                mDatabind.ivShowAd.setOnClickListener {
                    jumpOutUrl(bean.targetUrl)
                }

            })
        }

        appViewModel.appPolling.observe(this) {
            try {
                //防止数据未初始化的情况
                if (::matchDetail.isInitialized && matchDetail.status in 0..if (matchType == "1") 7 else 9) {
                    mViewModel.getMatchDetail(matchId, matchType)
                }
            } catch (_: Exception) {
            }

        }
    }

    private fun changeUI() {
        mDatabind.videoPlayer.visibleOrGone(isShowVideo)
        mDatabind.cslMatchStatus.visibleOrGone(!isShowVideo)
        mDatabind.tvToChat.visibleOrGone(isHasAnchor)
        setNewViewPager(
            signalPos,
            mTitles,
            mFragList,
            tabs,
            isHasAnchor,
            anchorId,
            matchDetail,
            pager2Adapter,
            mDatabind.viewPager,
            mDatabind.magicIndicator)
        //有主播
        if (isHasAnchor) {
            mDatabind.viewPager.postDelayed({ mViewModel.getDetailAnchorInfo(anchorId!!) },
                200)
        }
    }

    private fun getSignal(match: MatchDetailBean,  action: (String?) -> Unit = {}) {
        match.anchorList.notNull({ list ->
            for ((i, item) in list.withIndex()) {
                if (isHasAnchor) {
                    if (anchorId == item.userId) {
                        isShowVideo = true
                        signalPos = i
                        anchor = item
                        playUrl = item.playUrl
                        action.invoke(playUrl)
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
                                playUrl = item.playUrl
                                action.invoke(playUrl)
                            }
                        } else {
                            isShowVideo = true
                            isHasAnchor = true
                            anchor = item
                            playUrl = item.playUrl
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
            startVideo(playUrl)
        }
        //Log.e("TAG", "onResume: ${isTopActivity(this)}===" + this.toString())
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