package com.walisport.module.live.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.GONE
import androidx.constraintlayout.widget.ConstraintLayout.VISIBLE
import androidx.core.animation.doOnEnd
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ThreadUtils.mainScope
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.qyplayer.GlobalConfig
import arch.cayenne.lib.qyplayer.transformFromPlayerConfig
import arch.cayenne.lib.qyplayer.transformToPlayerConfig
import arch.cayenne.lib.qyplayer.ui.widget.LivePlayerView
import arch.cayenne.lib.skin.res.SkinnableResourceManager.getDrawable
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.ANIMATION_DURATION
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.HIDE_BUTTONS_TIMER
import com.walisport.module.live.databinding.FragmentLiveVideoLandscapeBinding
import com.walisport.module.live.ui.video.PlayerViewCache
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import com.xxx.qyplayer.DecryptMode
import com.xxx.qyplayer.PlayerMode
import com.xxx.qyplayer.PlayerState
import com.xxx.qyplayer.transformToInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

/**
 * 视频横屏播放页
 */
class LiveVideoLandscapeFragment :
    BaseFragment<LiveVideoViewModel, FragmentLiveVideoLandscapeBinding>(), CancelAdapt {

    override val vbClass: KClass<FragmentLiveVideoLandscapeBinding> =
        FragmentLiveVideoLandscapeBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    private lateinit var videoView: LivePlayerView

    private var videoViewFullScreen = true

    private var buttonsDisplaying = true

    /**
     * 隐藏操作栏的定时Job
     */
    private var scheduledHideButtonsJob: Job? = null

    /**
     * 视频加载时的动画
     */
    private var loadingAnim: ObjectAnimator? = null


    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        initVideoView()
        initBackPress()
        scheduleHideButtons()
    }

    private fun initVideoView() {
        //横屏一般是从竖屏过来的，可以直接复用之前的播放器实例
//        "landscape.initVideoView".logd("videoCache")
        videoView = PlayerViewCache.acquirePlayerView {
            LivePlayerView(requireActivity())
                .apply {
                    init(PlayerMode.FLUENCY)
                    keepScreenOn = true
                    setConfig(GlobalConfig(requireContext()).also {
                        if (!it.inited) {
                            // 首次启动从本地播放器获取默认配置
                            it.transformFromPlayerConfig(videoView.getConfig())
                            // 默认不加密
                            it.audioDecrypt = DecryptMode.DECRYPT_MODE_NONE.transformToInt()
                            it.videoDecrypt = DecryptMode.DECRYPT_MODE_NONE.transformToInt()
                            it.reconnectCount = -1 // Demo重试一百次, -1不限制
                            //默认不开启硬件加速
                            it.isHWDecode = false

                            it.inited = true
                        }
                    }.transformToPlayerConfig())

                }
        }

        //单击事件处理
        videoView.setOnSingleTapListener {
            if (videoViewFullScreen) {
                if (buttonsDisplaying) {
                    buttonsDisplaying = false

                    hideButtonsAnimated()
                } else {
                    buttonsDisplaying = true

                    showButtonsAnimated()
                }
            } else {
                enlarge {
                    showButtons()
                    videoViewFullScreen = true
                }


                //隐藏子fragment
                hideFragment()
            }
        }

        //播放状态处理
        videoView.setPlayerStateListener { onPlayerStateReceived(it) }

        // 创建 LayoutParams，设置宽度和高度为 match_parent
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // 宽度
            LinearLayout.LayoutParams.MATCH_PARENT  // 高度
        )

        // 将 LayoutParams 应用到 VideoView
        videoView.layoutParams = layoutParams

        if (videoView.parent != null) {
            (videoView.parent as ViewGroup).removeView(videoView)
        }

        mBinding.videoViewContainer.addView(videoView)
    }

    override fun initListener() {
        mBinding.ivBack.clickNoRepeat {
            findNavController().navigateUp()
        }

        mBinding.root.clickNoRepeat {
            if (videoViewFullScreen) {
                if (buttonsDisplaying) {
                    buttonsDisplaying = false

                    hideButtonsAnimated()
                } else {
                    buttonsDisplaying = true

                    showButtonsAnimated()
                }
            } else {
                enlarge {
                    showButtons()
                    videoViewFullScreen = true
                }

                //隐藏子fragment
                hideFragment()
            }
        }

        mBinding.ivVideoLandscapeTournamentIcon.clickNoRepeat {
            jumpToLeagueFragment()
        }

        mBinding.tvMatchName.clickNoRepeat {
            jumpToLeagueFragment()
        }

        mBinding.ivShare.clickNoRepeat {
            hideButtons()
            reduce(
                targetWidth = mBinding.root.measuredWidth - mBinding.fragmentShare.measuredWidth - VIDEO_MARGIN_HORIZONTAL.dp2px * 2,
                targetHeight = 275.dp2px,
                targetHorizontalMargin = VIDEO_MARGIN_HORIZONTAL.dp2px
            ) {
                videoViewFullScreen = false
            }
            setShareView()
            showShareView()
        }

        mBinding.llChooseSource.clickNoRepeat {
            hideButtons()
            reduce(
                targetWidth = mBinding.root.measuredWidth - mBinding.fragmentChooseSource.measuredWidth - VIDEO_MARGIN_HORIZONTAL.dp2px * 2,
                targetHeight = 275.dp2px,
                targetHorizontalMargin = VIDEO_MARGIN_HORIZONTAL.dp2px
            ) {
                videoViewFullScreen = false
            }

            setChooseSourceView()
            showChooseSourceView()
        }


        mBinding.tvStatistics.clickNoRepeat {
            hideButtons()
            reduce(
                targetWidth = mBinding.root.measuredWidth - mBinding.fragmentStatistics.measuredWidth - VIDEO_MARGIN_HORIZONTAL.dp2px * 2,
                targetHeight = 209.dp2px,
                targetHorizontalMargin = VIDEO_MARGIN_HORIZONTAL.dp2px
            ) {
                videoViewFullScreen = false
            }

            setStatisticsView()
            showStatisticsView()
        }

    }

    override fun createObserver() {
        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {

                    val playUrl = it.source.firstOrNull { ele -> ele.isPlaying }?.playUrl()
                    playUrl?.takeIf { url -> url.isNotEmpty() }?.let { url ->
                        videoView.setDataSource(url)
                        videoView.prepare()
                    }
                }
            }
        }
        mViewModel.tournamentIcon.observe(viewLifecycleOwner) {
//            "tournamentIcon: $it".logd("matchIssue")
            it?.takeIf { it.isNotEmpty() }?.let { url ->
                Glide.with(requireContext()).load(url)
                    .placeholder(R.drawable.title_league_icon)
                    .error(R.drawable.title_league_icon)
                    .into(mBinding.ivVideoLandscapeTournamentIcon)
            }
        }
        mViewModel.matchName.observe(viewLifecycleOwner) {
            it?.takeIf { it.isNotEmpty() }?.let { name ->
                mBinding.tvMatchName.text = name
            }
        }
        mViewModel.mainMatch.observe(viewLifecycleOwner) {
            it?.let {
                mViewModel.leagueID = it.basicInfo.tournamentId //联赛ID
            }
        }
        mViewModel.createObserver()
    }

    override fun initData() {
        super.initData()
        mViewModel.getMainMatch(mViewModel.matchId())
    }

    /**
     * 监听返回键
     */
    private fun initBackPress(){
        // 监听返回键
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    mBinding.videoViewContainer.removeAllViews()
                }
            })
    }


    /**
     * 展示上边和下边的操作按钮，不带动画
     */
    private fun showButtons() {
        mBinding.topArea.visibility = View.VISIBLE
        mBinding.bottomArea.visibility = View.VISIBLE
    }

    /**
     * 隐藏上边和下边的操作按钮，不带动画
     */
    private fun hideButtons() {
        mBinding.topArea.visibility = View.GONE
        mBinding.bottomArea.visibility = View.GONE
    }

    /**
     * 展示上边和下边的操作按钮，带动画
     */
    private fun showButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_landscape_operate_area_height).toFloat()

        with(AnimatorSet()) {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "translationY",
                    *floatArrayOf(-operateAreaHeight, 0f)
                ),
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "alpha",
                    *floatArrayOf(0.5f, 1f)
                ),
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "translationY",
                    *floatArrayOf(operateAreaHeight, 0f)
                ),
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "alpha",
                    *floatArrayOf(0.5f, 1f)
                ),

                )
            setDuration(ANIMATION_DURATION)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    scheduleHideButtons()
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            start()
        }
    }

    /**
     * 隐藏上边和下边的操作按钮，带动画
     */
    private fun hideButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_landscape_operate_area_height).toFloat()

        with(AnimatorSet()) {
            playTogether(
                ObjectAnimator.ofFloat(
                    mBinding.topArea,
                    "translationY",
                    0f, -operateAreaHeight
                ),
                ObjectAnimator.ofFloat(mBinding.topArea, "alpha", 1f, 0.5f),
                ObjectAnimator.ofFloat(
                    mBinding.bottomArea,
                    "translationY",
                    *floatArrayOf(0f, operateAreaHeight)
                ),
                ObjectAnimator.ofFloat(mBinding.bottomArea, "alpha", 1f, 0.5f),
            )
            setDuration(ANIMATION_DURATION)

            start()
        }
    }

    /**
     * 设置定时任务，隐藏操作栏
     */
    private fun scheduleHideButtons() {
        scheduledHideButtonsJob?.cancel()
        scheduledHideButtonsJob = mainScope.launch {
            delay(HIDE_BUTTONS_TIMER)

            buttonsDisplaying = false
            hideButtonsAnimated()
        }

    }

    /**
     * 放大视频播放区
     */
    private fun enlarge(onEndAction: () -> Unit) {
        //width， height， marginStart, marginTop
        val currentHeight = mBinding.videoViewContainer.measuredHeight
        val targetWidth = mBinding.root.measuredWidth
        val currentWidth = mBinding.videoViewContainer.measuredWidth
        val targetHeight = mBinding.root.measuredHeight
        val currentMarginTop =
            (mBinding.videoViewContainer.layoutParams as ConstraintLayout.LayoutParams).topMargin
        val targetMarginTop = 0
        val currentMarginStart =
            (mBinding.videoViewContainer.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0

        with(AnimatorSet()) {
            playTogether(
                ValueAnimator.ofInt(currentHeight, targetHeight).apply {
                    addUpdateListener {
                        val lp = mBinding.videoViewContainer.layoutParams
                        lp.height = it.animatedValue as Int

                        mBinding.videoViewContainer.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentWidth, targetWidth).apply {
                    addUpdateListener {
                        val lp = mBinding.videoViewContainer.layoutParams
                        lp.width = it.animatedValue as Int

                        mBinding.videoViewContainer.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentMarginTop, targetMarginTop).apply {
                    addUpdateListener {
                        val lp =
                            mBinding.videoViewContainer.layoutParams as ConstraintLayout.LayoutParams
                        lp.topMargin = it.animatedValue as Int

                        mBinding.videoViewContainer.layoutParams = lp

                    }
                },
                ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
                    addUpdateListener {
                        val lp =
                            mBinding.videoViewContainer.layoutParams as ConstraintLayout.LayoutParams
                        lp.marginStart = it.animatedValue as Int
                        lp.marginEnd = it.animatedValue as Int

                        mBinding.videoViewContainer.layoutParams = lp

                    }
                })
            setDuration(ANIMATION_DURATION)
            doOnEnd {
                mBinding.videoViewContainer.background =
                    getDrawable(requireContext(), arch.cayenne.lib.common.R.color.black)
                onEndAction()
            }
            start()
        }
    }

    /**
     * 缩小视频播放区
     */
    private fun reduce(
        targetWidth: Int,
        targetHeight: Int,
        targetHorizontalMargin: Int,
        onEndAction: () -> Unit
    ) {
        //width， height， marginStart, marginTop
        val currentHeight = mBinding.root.measuredHeight
        val currentWidth = mBinding.root.measuredWidth
        val currentMarginTop = 0
        val targetMarginTop = (currentHeight - targetHeight) / 2
        val currentMarginStart = 0

        with(AnimatorSet()) {
            playTogether(
                ValueAnimator.ofInt(currentHeight, targetHeight).apply {
                    addUpdateListener {
                        val lp = mBinding.videoViewContainer.layoutParams
                        lp.height = it.animatedValue as Int

                        mBinding.videoViewContainer.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentWidth, targetWidth).apply {
                    addUpdateListener {
                        val lp = mBinding.videoViewContainer.layoutParams
                        lp.width = it.animatedValue as Int

                        mBinding.videoViewContainer.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentMarginTop, targetMarginTop).apply {
                    addUpdateListener {
                        val lp =
                            mBinding.videoViewContainer.layoutParams as ConstraintLayout.LayoutParams
                        lp.topMargin = it.animatedValue as Int

                        mBinding.videoViewContainer.layoutParams = lp

                    }
                },
                ValueAnimator.ofInt(currentMarginStart, targetHorizontalMargin).apply {
                    addUpdateListener {
                        val lp =
                            mBinding.videoViewContainer.layoutParams as ConstraintLayout.LayoutParams
                        lp.marginStart = it.animatedValue as Int
                        lp.marginEnd = it.animatedValue as Int

                        mBinding.videoViewContainer.layoutParams = lp

                    }
                })
            setDuration(ANIMATION_DURATION)
            doOnEnd {
                mBinding.videoViewContainer.background =
                    getDrawable(requireContext(), R.drawable.bg_shape_video_view_reduced)
                onEndAction()
            }
            start()
        }

    }

    override fun onStop() {
        super.onStop()

        videoView.onStop()
    }

    override fun onResume() {
//        "onResume".logd(TAG)
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        //使用横屏时到宽高
        AutoSizeConfig.getInstance().setDesignWidthInDp(LANDSCAPE_WIDTH)
        AutoSizeConfig.getInstance().setDesignHeightInDp(LANDSCAPE_HEIGHT)
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        setStatusBar(StatusBarConfig, mBinding.root)
        videoView.onResume()
    }

    override fun onPause() {
//        "onPause".logd(TAG)
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        //恢复竖屏，宽高也要回到竖屏时到宽高
        AutoSizeConfig.getInstance().setDesignWidthInDp(PORTRAIT_WIDTH)
        AutoSizeConfig.getInstance().setDesignHeightInDp(PORTRAIT_HEIGHT)
        videoView.onPause()
    }

    override fun onDestroy() {
//        "landscape.onDestroy".logd("videoCache")
        super.onDestroy()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND
        setStatusBar(StatusBarConfig, mBinding.root)
        PlayerViewCache.releasePlayerView(videoView) {
            it.onDestroy()
        }
    }


    /**
     * 跳转到联赛赛程页
     */
    private fun jumpToLeagueFragment() {
        navigate(
            LiveVideoLandscapeFragmentDirections.actionLiveVideoLandscapeFragmentToLeagueFragment()
                .apply {
                    arguments.putLong("matchID", mViewModel.matchId())
                    arguments.putInt("leagueID", mViewModel.leagueID)
                })
    }

    /**
     *  创建视频分享页
     */
    private fun setShareView() {
        childFragmentManager.findFragmentByTag(LiveVideoShareFragment.TAG)
                as? LiveVideoShareFragment ?: LiveVideoShareFragment().also {
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentShare.id, it, LiveVideoShareFragment.TAG)
                .commitNow()
        }

    }

    /**
     * 视频分享页入场动画
     */
    private fun showShareView() {
        val currentMarginStart =
            (mBinding.fragmentShare.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = -mBinding.fragmentShare.measuredWidth
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentShare.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int
                mBinding.fragmentShare.layoutParams = lp
            }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 视频分享页退场动画
     */
    private fun hideShareView(onEndAction: () -> Unit) {
        val currentMarginStart =
            (mBinding.fragmentShare.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentShare.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentShare.layoutParams = lp

            }
            doOnEnd { onEndAction() }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 创建选择视频源页
     */
    private fun setChooseSourceView() {
        childFragmentManager.findFragmentByTag(LiveVideoSourceLandscapeFragment.TAG)
                as? LiveVideoSourceLandscapeFragment ?: LiveVideoSourceLandscapeFragment().also {
            it.arguments = Bundle().apply {
                putLong("matchId", mViewModel.matchId())
            }
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentChooseSource.id, it, LiveVideoSourceLandscapeFragment.TAG)
                .commitNow()
        }

    }

    /**
     *视频源页入场动画
     */
    private fun showChooseSourceView() {
        val currentMarginStart =
            (mBinding.fragmentChooseSource.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = -mBinding.fragmentChooseSource.measuredWidth

        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentChooseSource.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentChooseSource.layoutParams = lp

            }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 视频源页面退场动画
     */
    private fun hideChooseSourceView(onEndAction: () -> Unit) {
        val currentMarginStart =
            (mBinding.fragmentChooseSource.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentChooseSource.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentChooseSource.layoutParams = lp

            }
            doOnEnd { onEndAction() }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 创建赛况统计页
     */
    private fun setStatisticsView() {
        childFragmentManager.findFragmentByTag(LiveVideoStatisticsFragment.TAG)
                as? LiveVideoStatisticsFragment ?: LiveVideoStatisticsFragment().also {
            it.arguments = Bundle().apply { putLong("matchId", mViewModel.matchId()) }
            childFragmentManager.beginTransaction()
                .replace(mBinding.fragmentStatistics.id, it, LiveVideoStatisticsFragment.TAG)
                .commitNow()
        }

    }

    /**
     * 赛况页入场动画
     */
    private fun showStatisticsView() {
        val currentMarginStart =
            (mBinding.fragmentStatistics.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = -mBinding.fragmentStatistics.measuredWidth
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentStatistics.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentStatistics.layoutParams = lp

            }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 赛况页退场动画
     */
    private fun hideStatisticsView(onEndAction: () -> Unit) {
        val currentMarginStart =
            (mBinding.fragmentStatistics.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0
        ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
            addUpdateListener {
                val lp = mBinding.fragmentStatistics.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = it.animatedValue as Int

                mBinding.fragmentStatistics.layoutParams = lp

            }
            doOnEnd { onEndAction() }
            setDuration(ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 移除分享页，视频源页，赛况页
     */
    private fun hideFragment() {
        val fragment = childFragmentManager.findFragmentByTag(LiveVideoShareFragment.TAG)

        if (fragment is LiveVideoShareFragment) {
            hideShareView {
                childFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }

        val fragment2 = childFragmentManager.findFragmentByTag(LiveVideoSourceLandscapeFragment.TAG)

        if (fragment2 is LiveVideoSourceLandscapeFragment) {
            hideChooseSourceView {
                childFragmentManager.beginTransaction().remove(fragment2).commit()
            }
        }

        val fragment3 = childFragmentManager.findFragmentByTag(LiveVideoStatisticsFragment.TAG)

        if (fragment3 is LiveVideoStatisticsFragment) {
            hideStatisticsView {
                childFragmentManager.beginTransaction().remove(fragment3).commit()
            }
        }


    }

    private fun onPlayerStateReceived(state: PlayerState) {

        when (state) {
            PlayerState.PLAYING -> {
                loadingAnim?.cancel()
                mBinding.ctLoading.visibility = GONE
                mBinding.ctError.visibility = GONE
            }

            PlayerState.PAUSED -> {
                //没有暂停按钮，
            }

            PlayerState.CACHING, PlayerState.CONNECTING -> {
                // 创建旋转动画
                loadingAnim = ObjectAnimator.ofFloat(
                    mBinding.ivVideoLoading,  // 目标 View
                    "rotation",  // 属性名称
                    0f, 360f // 从 0 度旋转到 360 度
                ).run {
                    // 设置动画属性
                    setDuration(1000) // 持续时间 1 秒
                    repeatCount = ObjectAnimator.INFINITE // 无限循环
                    interpolator = LinearInterpolator() // 匀速旋转

                    // 启动动画
                    start()
                    this
                }

                mBinding.ctLoading.visibility = VISIBLE
                mBinding.ctError.visibility = GONE

            }

            PlayerState.ERROR -> {
                mBinding.ctLoading.visibility = GONE
                mBinding.ctError.visibility = VISIBLE
            }

            PlayerState.STOPPED -> {
                loadingAnim?.cancel()
                mBinding.ctLoading.visibility = GONE
                mBinding.ctError.visibility = GONE
            }

            else -> {
                loadingAnim?.cancel()
                mBinding.ctLoading.visibility = GONE
                mBinding.ctError.visibility = GONE
            }
        }

    }

    companion object {

        const val LANDSCAPE_WIDTH = 812
        const val LANDSCAPE_HEIGHT = 375

        const val PORTRAIT_WIDTH = 375
        const val PORTRAIT_HEIGHT = 812

        const val VIDEO_MARGIN_HORIZONTAL = 32
    }

}