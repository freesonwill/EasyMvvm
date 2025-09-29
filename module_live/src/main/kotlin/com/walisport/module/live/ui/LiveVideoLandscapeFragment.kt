package com.walisport.module.live.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.ActivityInfo
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.GONE
import androidx.constraintlayout.widget.ConstraintLayout.VISIBLE
import androidx.core.animation.doOnEnd
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.DensityInfo
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimensionPixelSize
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import arch.cayenne.lib.qyplayer.GlobalConfig
import arch.cayenne.lib.qyplayer.transformFromPlayerConfig
import arch.cayenne.lib.qyplayer.transformToPlayerConfig
import arch.cayenne.lib.qyplayer.ui.widget.LivePlayerView
import arch.cayenne.lib.qyplayer.ui.widget.QYRenderView
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.BUTTONS_ANIMATION_DURATION
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.HIDE_BUTTONS_TIMER
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.ZOOM_ANIMATION_DURATION
import com.walisport.module.live.databinding.FragmentLiveVideoLandscapeBinding
import com.walisport.module.live.ui.video.PlayerViewCache
import com.walisport.module.live.ui.viewmodel.LiveVideoPlayerViewModel
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
    BaseFragment<LiveVideoPlayerViewModel, FragmentLiveVideoLandscapeBinding>(), CancelAdapt {

    override val vbClass: KClass<FragmentLiveVideoLandscapeBinding> =
        FragmentLiveVideoLandscapeBinding::class
    override val vmClass: KClass<LiveVideoPlayerViewModel> = LiveVideoPlayerViewModel::class

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

    private lateinit var audioManager: AudioManager
    private var volumeObserver: VolumeObserver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化 AudioManager
        audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // 初始化 VolumeObserver
        volumeObserver = VolumeObserver(Handler(Looper.getMainLooper()))
    }


    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        initVideoView()
        scheduleHideButtons()

        view?.postDelayed({
            setStatisticsView()
            setChooseSourceView()
            setShareView()
        }, 200)


        initMargins()
    }

    //调整按钮的margin值， 保证其位于视频播放区域内
    private fun initMargins() {
        var screenWidth = resources.displayMetrics.widthPixels
        var screenHeight = resources.displayMetrics.heightPixels


        //调整成横屏的宽高
        if (screenWidth < screenHeight) {
            val a = screenWidth
            screenWidth = screenHeight
            screenHeight = a
        }

        screenWidth += ViewUtils.getStatusBarHeight(requireContext())
        

        val videoAreaWidth = screenHeight / 1080L * 1920L
        val leftSpacing = (screenWidth - videoAreaWidth) / 2

        if (leftSpacing <= 0) {
            return
        }

        val initialMarginStart = arch.cayenne.lib.common.R.dimen.dp_36.getDimensionPixelSize()
        val lp = mBinding.ivBack.layoutParams as ConstraintLayout.LayoutParams
        lp.marginStart = (initialMarginStart + leftSpacing).toInt()
        mBinding.ivBack.layoutParams = lp


        val shareLp = mBinding.ivShare.layoutParams as ConstraintLayout.LayoutParams
        shareLp.marginEnd = (initialMarginStart + leftSpacing).toInt()
        mBinding.ivShare.layoutParams = shareLp

        val soundLp = mBinding.ivSoundToggle.layoutParams as ConstraintLayout.LayoutParams
        soundLp.marginStart = (initialMarginStart + leftSpacing).toInt()
        mBinding.ivSoundToggle.layoutParams = soundLp

        val statisticLp = mBinding.tvStatistics.layoutParams as ConstraintLayout.LayoutParams
        statisticLp.marginEnd = (initialMarginStart + leftSpacing).toInt()
        mBinding.tvStatistics.layoutParams = statisticLp

    }

    private fun initVideoView() {
        //横屏一般是从竖屏过来的，可以直接复用之前的播放器实例
//        "landscape.initVideoView".logd("videoCache")
        videoView = PlayerViewCache.acquirePlayerView {
            LivePlayerView(requireActivity())
                .apply {
                    init(PlayerMode.FLUENCY)
                    keepScreenOn = true
                    setConfig(GlobalConfig().also {
                        if (!it.inited) {
                            // 首次启动从本地播放器获取默认配置
                            it.transformFromPlayerConfig(this.getConfig())
                            // 默认不加密
                            it.audioDecrypt = DecryptMode.DECRYPT_MODE_NONE.transformToInt()
                            it.videoDecrypt = DecryptMode.DECRYPT_MODE_NONE.transformToInt()
                            it.reconnectCount = -1 // Demo重试一百次, -1不限制
                            //开启硬件加速
                            it.isHWDecode = true

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
        videoView.setPlayerStateListener {
            mViewModel.setPlayerState(it)
        }

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
        mBinding.ivBack.addScaleOnTouchAnimation()
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

        mBinding.ivShare.addScaleOnTouchAnimation()
        mBinding.ivShare.clickNoRepeat {
            hideButtons()
            reduce(
                targetWidth = mBinding.root.measuredWidth - mBinding.fragmentShare.measuredWidth - VIDEO_MARGIN_HORIZONTAL.dp2px * 2,
                targetHeight = 275.dp2px,
                targetHorizontalMargin = VIDEO_MARGIN_HORIZONTAL.dp2px
            ) {
                videoViewFullScreen = false
            }
            showShareView()
        }

        mBinding.llChooseSource.addScaleOnTouchAnimation()
        mBinding.llChooseSource.clickNoRepeat {
            hideButtons()
            reduce(
                targetWidth = mBinding.root.measuredWidth - mBinding.fragmentChooseSource.measuredWidth - VIDEO_MARGIN_HORIZONTAL.dp2px * 2,
                targetHeight = 275.dp2px,
                targetHorizontalMargin = VIDEO_MARGIN_HORIZONTAL.dp2px
            ) {
                videoViewFullScreen = false
            }

            showChooseSourceView()
        }

        mBinding.tvStatistics.addScaleOnTouchAnimation()
        mBinding.tvStatistics.clickNoRepeat {
            hideButtons()
            reduce(
                targetWidth = mBinding.root.measuredWidth - mBinding.fragmentStatistics.measuredWidth - VIDEO_MARGIN_HORIZONTAL.dp2px * 2,
                targetHeight = 209.dp2px,
                targetHorizontalMargin = (VIDEO_MARGIN_HORIZONTAL.dp2px * 1.6).toInt()
            ) {
                videoViewFullScreen = false
            }

            setStatisticsView()
            showStatisticsView()
        }

        mBinding.ivSoundToggle.addScaleOnTouchAnimation()
        mBinding.ivSoundToggle.clickNoRepeat {
            scheduleHideButtons()
            mViewModel.changeMuteStatus()
        }

    }

    override suspend fun createObserver() {
        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {
                    val playUrl =
                        it.source.firstOrNull { ele -> ele.isPlaying }?.liveStreams?.firstOrNull { ele -> ele.selected }
                            ?.playUrl()

                    playUrl?.takeIf { url -> url.isNotEmpty() }?.let { url ->
                        if (url != videoView.getDataSource()) {
                            videoView.setDataSource(url)
                            videoView.prepare()
                        }
                    }
                }
            }

            tournamentIcon.observe(viewLifecycleOwner) {
//            "tournamentIcon: $it".logd("matchIssue")
                it?.takeIf { it.isNotEmpty() }?.let { url ->
                    Glide.with(requireContext()).load(url)
                        .placeholder(R.drawable.title_league_icon)
                        .error(R.drawable.title_league_icon)
                        .into(mBinding.ivVideoLandscapeTournamentIcon)
                }
            }
            matchName.observe(viewLifecycleOwner) {
                it?.takeIf { it.isNotEmpty() }?.let { name ->
                    mBinding.tvMatchName.text = name
                }
            }
            mainMatch.observe(viewLifecycleOwner) {
                it?.let {
                    mViewModel.leagueID = it.basicInfo.tournamentId //联赛ID
                }
            }

            playerState.observe(viewLifecycleOwner) {
                onPlayerStateReceived(it)
            }

            mutedData().observe(viewLifecycleOwner) {
                mBinding.ivSoundToggle.setImageResource(
                    if (it) R.drawable.shape_muted else R.drawable.shape_immuted
                )

                videoView.setMute(it)
            }
        }

        mViewModel.createObserver()
    }

    override fun initData() {
        super.initData()
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
            resources.getDimensionPixelSize(R.dimen.video_landscape_operate_area_height_top).toFloat()

        val operateAreaHeightBottom =
            resources.getDimensionPixelSize(R.dimen.video_landscape_operate_area_height_bottom).toFloat()

        mBinding.root.startSafeAnimateSet({
            playTogether(
                mBinding.topArea.startSafeObjectAnimator(
                    "translationY",
                    *floatArrayOf(-operateAreaHeight, 0f)
                ),
                mBinding.topArea.startSafeObjectAnimator(
                    "alpha",
                    *floatArrayOf(0.5f, 1f)
                ),
                mBinding.bottomArea.startSafeObjectAnimator(
                    "translationY",
                    *floatArrayOf(operateAreaHeightBottom, 0f)
                ),
                mBinding.bottomArea.startSafeObjectAnimator(
                    "alpha",
                    *floatArrayOf(0.5f, 1f)
                ),

                )
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
        }, duration = BUTTONS_ANIMATION_DURATION, start = true)
    }

    /**
     * 隐藏上边和下边的操作按钮，带动画
     */
    private fun hideButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_landscape_operate_area_height_top).toFloat()

        val operateAreaHeightBottom =
            resources.getDimensionPixelSize(R.dimen.video_landscape_operate_area_height_bottom).toFloat()

        mBinding.root.startSafeAnimateSet({
            playTogether(
                mBinding.topArea.startSafeObjectAnimator(
                    "translationY",
                    0f, -operateAreaHeight
                ),
                mBinding.topArea.startSafeObjectAnimator("alpha", 1f, 0.5f),
                mBinding.bottomArea.startSafeObjectAnimator(
                    "translationY",
                    *floatArrayOf(0f, operateAreaHeightBottom)
                ),
                mBinding.bottomArea.startSafeObjectAnimator("alpha", 1f, 0.5f),
            )
        }, duration = BUTTONS_ANIMATION_DURATION, start = true)
    }

    /**
     * 设置定时任务，隐藏操作栏
     */
    private fun scheduleHideButtons() {
        scheduledHideButtonsJob?.cancel()
        scheduledHideButtonsJob = lifecycleScope.launch {
            delay(HIDE_BUTTONS_TIMER)

            buttonsDisplaying = false
            hideButtonsAnimated()
        }

    }

    /**
     * 放大视频播放区
     */
    private fun enlarge(onEndAction: () -> Unit) {
        val renderView: QYRenderView =
            mBinding.root.findViewById(arch.cayenne.lib.qyplayer.R.id.renderView)

        val currentScaleX = renderView.scaleX
        val targetScaleX = 1.0f
        val currentScaleY = renderView.scaleY
        val targetScaleY = 1.0f

        val currentTranslationX = renderView.translationX
        val targetTranslationX = 0f
        val currentTranslationY = renderView.translationY
        val targetTranslationY = 0f

        mBinding.root.startSafeAnimateSet({
            playTogether(
                ValueAnimator.ofFloat(currentScaleX, targetScaleX).apply {
                    addUpdateListener {
                        renderView.scaleX = it.animatedValue as Float
                        mBinding.ctLoading.scaleX = it.animatedValue as Float
                        mBinding.ctError.scaleX = it.animatedValue as Float
                    }
                },
                ValueAnimator.ofFloat(currentScaleY, targetScaleY).apply {
                    addUpdateListener {
                        renderView.scaleY = it.animatedValue as Float
                        mBinding.ctLoading.scaleY = it.animatedValue as Float
                        mBinding.ctError.scaleY = it.animatedValue as Float
                    }
                },
                ValueAnimator.ofFloat(currentTranslationX, targetTranslationX).apply {
                    addUpdateListener {
                        renderView.translationX = it.animatedValue as Float
                        mBinding.ctLoading.translationX = it.animatedValue as Float
                        mBinding.ctError.translationX = it.animatedValue as Float
                    }
                },
//                ValueAnimator.ofFloat(currentTranslationY, targetTranslationY).apply {
//                    addUpdateListener {
//                        renderView.translationY = it.animatedValue as Float
//                        mBinding.ctLoading.translationY = it.animatedValue as Float
//                        mBinding.ctError.translationY = it.animatedValue as Float
//                    }
//                }
            )
            doOnEnd {
                onEndAction()
            }
        }, duration = ZOOM_ANIMATION_DURATION, start = true)
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

        val renderView: QYRenderView =
            mBinding.root.findViewById(arch.cayenne.lib.qyplayer.R.id.renderView)

        val currentHeight = mBinding.root.measuredHeight
        val currentWidth = mBinding.root.measuredWidth
        val currentScaleX = 1.0f
        val targetScaleX = targetWidth.toFloat() / currentWidth

        val currentScaleY = 1.0f
        val targetScaleY = targetHeight.toFloat() / currentHeight

        val currentTranslationX = 0f
        val targetTranslationX = -targetHorizontalMargin.toFloat() * 4

        val currentTranslationY = 0f
        val targetTranslationY = (currentHeight.toFloat() - targetHeight) / 2


        mBinding.root.startSafeAnimateSet({
            playTogether(
                ValueAnimator.ofFloat(currentScaleX, targetScaleX).apply {
                    addUpdateListener {
                        renderView.scaleX = it.animatedValue as Float
                        mBinding.ctLoading.scaleX = it.animatedValue as Float
                        mBinding.ctError.scaleX = it.animatedValue as Float
                    }
                },
                ValueAnimator.ofFloat(currentScaleY, targetScaleY).apply {
                    addUpdateListener {
                        renderView.scaleY = it.animatedValue as Float
                        mBinding.ctLoading.scaleY = it.animatedValue as Float
                        mBinding.ctError.scaleY = it.animatedValue as Float
                    }
                },
                ValueAnimator.ofFloat(currentTranslationX, targetTranslationX).apply {
                    addUpdateListener {
                        renderView.translationX = it.animatedValue as Float
                        mBinding.ctLoading.translationX = it.animatedValue as Float
                        mBinding.ctError.translationX = it.animatedValue as Float

                    }
                },
//                ValueAnimator.ofFloat(currentTranslationY, targetTranslationY).apply {
//                    addUpdateListener {
//                    }
//                }
            )
            doOnEnd {
                onEndAction()
            }
        }, duration = ZOOM_ANIMATION_DURATION, start = true)

    }

    override fun onStop() {
        super.onStop()
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
        if (videoView.parent == mBinding.videoViewContainer) {
            videoView.onPause()
        }
    }

    override fun onDestroyView() {
//        "landscape.onDestroyView".logd("videoCache")
        super.onDestroyView()
        PlayerViewCache.releasePlayerView(videoView) {
            it.onDestroy()
        }
        mViewModel.landscapeVideoFragmentDestroyedEvent().value = true
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
        val metrics = resources.displayMetrics
        //density和scaledDensity被篡改，尝试恢复
        if (metrics.density != DensityInfo.density && DensityInfo.density > 0) {
            metrics.density = DensityInfo.density
        }
        if (metrics.scaledDensity != DensityInfo.scaledDensity && DensityInfo.scaledDensity > 0) {
            metrics.scaledDensity = DensityInfo.scaledDensity
        }
        //退出后恢复竖屏
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
        val currentTranslationX = 0f
        val targetTranslationX = -mBinding.fragmentShare.measuredWidth.toFloat()
        ValueAnimator.ofFloat(currentTranslationX, targetTranslationX).apply {
            addUpdateListener {
                mBinding.fragmentShare.translationX = it.animatedValue as Float
            }
            setDuration(ZOOM_ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 视频分享页退场动画
     */
    private fun hideShareView(onEndAction: () -> Unit) {
        val currentTranslationX = mBinding.fragmentShare.translationX
        val targetTranslationX = 0f
        ValueAnimator.ofFloat(currentTranslationX, targetTranslationX).apply {
            addUpdateListener {
                mBinding.fragmentShare.translationX = it.animatedValue as Float

                "mBinding.fragmentShare.translationX :${mBinding.fragmentShare.translationX}".logd("animIssue")

            }
            doOnEnd { onEndAction() }
            setDuration(ZOOM_ANIMATION_DURATION)
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
        val currentTranslationX = 0f
        val targetTranslationX = -mBinding.fragmentChooseSource.measuredWidth.toFloat()

        ValueAnimator.ofFloat(currentTranslationX, targetTranslationX).apply {
            addUpdateListener {
                mBinding.fragmentChooseSource.translationX = it.animatedValue as Float
            }
            setDuration(ZOOM_ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 视频源页面退场动画
     */
    private fun hideChooseSourceView(onEndAction: () -> Unit) {
        val currentTranslationX = mBinding.fragmentChooseSource.translationX
        val targetTranslationX = 0f
        ValueAnimator.ofFloat(currentTranslationX, targetTranslationX).apply {
            addUpdateListener {
                mBinding.fragmentChooseSource.translationX = it.animatedValue as Float

                "mBinding.fragmentChooseSource.translationX:${mBinding.fragmentChooseSource.translationX}".logd(
                    "animIssue"
                )
            }
            doOnEnd { onEndAction() }
            setDuration(ZOOM_ANIMATION_DURATION)
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
        val currentTranslationX = 0f
        val targetTranslationX = -mBinding.fragmentStatistics.measuredWidth.toFloat()
        ValueAnimator.ofFloat(currentTranslationX, targetTranslationX).apply {
            addUpdateListener {
                mBinding.fragmentStatistics.translationX = it.animatedValue as Float

            }
            setDuration(ZOOM_ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 赛况页退场动画
     */
    private fun hideStatisticsView(onEndAction: () -> Unit) {
        val currentTranslationX = mBinding.fragmentStatistics.translationX
        val targetTranslationX = 0f
        ValueAnimator.ofFloat(currentTranslationX, targetTranslationX).apply {
            addUpdateListener {


                mBinding.fragmentStatistics.translationX = it.animatedValue as Float

            }
            doOnEnd { onEndAction() }
            setDuration(ZOOM_ANIMATION_DURATION)
            start()
        }
    }

    /**
     * 移除分享页，视频源页，赛况页
     */
    private fun hideFragment() {
        if (mBinding.fragmentShare.translationX.toInt() != 0) {
            hideShareView {
            }
        }

        if (mBinding.fragmentChooseSource.translationX.toInt() != 0) {
            hideChooseSourceView {
            }
        }

        if (mBinding.fragmentStatistics.translationX.toInt() != 0) {
            hideStatisticsView {
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

            PlayerState.CONNECTING -> {
                // 创建旋转动画
                loadingAnim = mBinding.ivVideoLoading.startSafeObjectAnimator(
                    "rotation",  // 属性名称
                    0f, 360f // 从 0 度旋转到 360 度
                ).run {
                    // 设置动画属性
                    setDuration(1500) // 持续时间 1.5 秒
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

    private inner class VolumeObserver(handler: Handler) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            // 获取当前媒体音量
            val mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            // 可以获取其他音量类型，如铃声：STREAM_RING，闹钟：STREAM_ALARM 等
            "媒体音量变化: $mediaVolume".logd(TAG)

            // 在这里添加音量变化后的处理逻辑
            // 例如：更新 UI 或触发其他操作
            if (mediaVolume > 0) {
                mViewModel.unMute()
            } else {
                mViewModel.mute()
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