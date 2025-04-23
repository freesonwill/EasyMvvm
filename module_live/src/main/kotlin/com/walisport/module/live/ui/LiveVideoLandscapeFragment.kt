package com.walisport.module.live.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.StatusBarConfig
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SportSkinResourceManager.getDrawable
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.PlayStatus
import com.walisport.module.live.databinding.FragmentLiveVideoLandscapeBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.internal.CancelAdapt
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import kotlin.reflect.KClass

/**
 * 视频横屏播放页
 */
class LiveVideoLandscapeFragment :
    BaseFragment<LiveVideoViewModel, FragmentLiveVideoLandscapeBinding>(), CancelAdapt {

    override val vbClass: KClass<FragmentLiveVideoLandscapeBinding> =
        FragmentLiveVideoLandscapeBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    private var mBackPressed = false

    private var videoViewFullScreen = true

    private var buttonsDisplaying = true

    private val playingStatusLiveData: MutableLiveData<PlayStatus> =
        MutableLiveData(PlayStatus.Loading)

    private var loadingAnim: ObjectAnimator? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var bufferingTimeoutJob: Job? = null


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }


    override fun initView(savedInstanceState: Bundle?) {
//        mViewModel.addMockData()
        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        val mediaPlayer = mBinding.videoView.mediaPlayer
        if (mediaPlayer is IjkMediaPlayer) {
            mediaPlayer.setOption(
                IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                "timeout",
                10000000
            ); // 10秒总超时（微秒）
            mediaPlayer
                .setOption(
                    IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                    "connect_timeout",
                    5000
                ); // 5秒连接超时（毫秒）
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 0); // 禁用自动重连
        }


        mBinding.videoView.setOnInfoListener { mp, what, extra ->
            when (what) {
                IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    // 视频开始缓冲（加载中）
//                    "Buffering started".logd(TAG)
                    playingStatusLiveData.postValue(PlayStatus.Loading)

                    // 启动协程，10秒超时
                    bufferingTimeoutJob = coroutineScope.launch {
                        delay(10000)
                        playingStatusLiveData.postValue(PlayStatus.Error)
                    }

                }

                IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    // 视频缓冲结束（加载完成，可以播放）
//                    "Buffering ended".logd(TAG)
                    playingStatusLiveData.postValue(PlayStatus.Playing)
                    bufferingTimeoutJob?.cancel()

                }

                IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    // 视频开始渲染（第一帧显示）
//                    "Video rendering started".logd(TAG)
                    playingStatusLiveData.postValue(PlayStatus.Playing)
                    bufferingTimeoutJob?.cancel()
                }

                IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START -> {
                    // 音频开始渲染
                    //  LogUtils.i(TAG, "Audio rendering started")
                }

                else -> {
                    "player info. what:${what}".logd(TAG)
                }
            }

            true
        }

        mBinding.videoView.setOnErrorListener { mp, what, extra ->
            when (what) {
                IjkMediaPlayer.MEDIA_ERROR_IO -> {
                    "Error: Network I/O error (MEDIA_ERROR_IO), extra: $extra".logd(TAG)
                    playingStatusLiveData.postValue(PlayStatus.Error)
                }

                IjkMediaPlayer.MEDIA_ERROR_MALFORMED -> {
                    "Error: Malformed stream (MEDIA_ERROR_MALFORMED), extra: $extra".logd(TAG)
                    playingStatusLiveData.postValue(PlayStatus.Error)
                }

                IjkMediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                    "Error: Unsupported format (MEDIA_ERROR_UNSUPPORTED), extra: $extra".logd(TAG)
                    playingStatusLiveData.postValue(PlayStatus.Error)
                }

                IjkMediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
                    "Error: Timeout (MEDIA_ERROR_TIMED_OUT), extra: $extra".logd(TAG)
                    playingStatusLiveData.postValue(PlayStatus.Error)
                }

                else -> {
                    "Unknown error, what: $what, extra: $extra".logd(TAG)
                    playingStatusLiveData.postValue(PlayStatus.Error)
                }
            }
            // 返回 true 表示错误已处理，false 表示未处理
            true
        }
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

        mBinding.ivVideoLandscapeLeagueIcon.clickNoRepeat {
            jumpToLeagueFragment()
        }

        mBinding.tvCompetitionName.clickNoRepeat {
            jumpToLeagueFragment()
        }

        mBinding.ivShare.clickNoRepeat {
            hideButtons()
            reduce {
                videoViewFullScreen = false
            }
            setShareView()
            showShareView()
        }

        mBinding.llChooseSource.clickNoRepeat {
            hideButtons()
            reduce {
                videoViewFullScreen = false
            }

            setChooseSourceView()
            showChooseSourceView()
        }


        mBinding.tvStatistics.clickNoRepeat {
            hideButtons()
            reduce(
                targetWidth = 376.dp2px,
                targetHeight = 209.dp2px,
                targetMarginStart = 32.dp2px
            ) {
                videoViewFullScreen = false
            }

            setStatisticsView()
            showStatisticsView()
        }

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
     * 放大视频播放区
     */
    private fun enlarge(onEndAction: () -> Unit) {
        //width， height， marginStart, marginTop
        val currentHeight = mBinding.videoView.measuredHeight
        val targetWidth = mBinding.root.measuredWidth
        val currentWidth = mBinding.videoView.measuredWidth
        val targetHeight = mBinding.root.measuredHeight
        val currentMarginTop =
            (mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams).topMargin
        val targetMarginTop = 0
        val currentMarginStart =
            (mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams).marginStart
        val targetMarginStart = 0

        with(AnimatorSet()) {
            playTogether(
                ValueAnimator.ofInt(currentHeight, targetHeight).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams
                        lp.height = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentWidth, targetWidth).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams
                        lp.width = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentMarginTop, targetMarginTop).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams
                        lp.topMargin = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp

                    }
                },
                ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams
                        lp.marginStart = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp

                    }
                })
            setDuration(ANIMATION_DURATION)
            doOnEnd {
                mBinding.videoView.background =
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
        targetWidth: Int = 495.dp2px,
        targetHeight: Int = 275.dp2px,
        targetMarginStart: Int = 32.dp2px,
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
                        val lp = mBinding.videoView.layoutParams
                        lp.height = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentWidth, targetWidth).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams
                        lp.width = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp
                    }
                },
                ValueAnimator.ofInt(currentMarginTop, targetMarginTop).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams
                        lp.topMargin = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp

                    }
                },
                ValueAnimator.ofInt(currentMarginStart, targetMarginStart).apply {
                    addUpdateListener {
                        val lp = mBinding.videoView.layoutParams as ConstraintLayout.LayoutParams
                        lp.marginStart = it.animatedValue as Int

                        mBinding.videoView.layoutParams = lp

                    }
                })
            setDuration(ANIMATION_DURATION)
            doOnEnd {
                mBinding.videoView.background =
                    getDrawable(requireContext(), R.drawable.bg_shape_video_view_reduced)
                onEndAction()
            }
            start()
        }

    }


    override fun createObserver() {
        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {

                    val playUrl = it.source.firstOrNull { ele -> ele.isPlaying }?.playUrl()
                    playUrl?.takeIf { url -> url.isNotEmpty() }.let { url ->
                        mBinding.videoView.setVideoURI(Uri.parse(url))
                        mBinding.videoView.start()
                    }
                }
            }
        }



        mViewModel.leagueIconUrl.observe(this) {
            Glide.with(mBinding.ivVideoLandscapeLeagueIcon).load(it)
                .placeholder(R.drawable.title_league_icon)
                .into(mBinding.ivVideoLandscapeLeagueIcon)
        }

        mViewModel.playerAName.observe(this) {
            mBinding.tvCompetitionName.text = "$it vs ${mViewModel.playerBName.value}"
        }

        playingStatusLiveData.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    PlayStatus.Playing -> {
                        loadingAnim?.cancel()
                        mBinding.includedLandscapeCtLoading.ctLoading.visibility = View.GONE
                        mBinding.includedLandscapeCtError.ctError.visibility = View.GONE
                    }

                    PlayStatus.Loading -> {
                        // 创建旋转动画
                        loadingAnim = ObjectAnimator.ofFloat(
                            mBinding.includedLandscapeCtLoading.ivVideoLoading,  // 目标 View
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

                        mBinding.includedLandscapeCtLoading.ctLoading.visibility = View.VISIBLE
                        mBinding.includedLandscapeCtError.ctError.visibility = View.GONE
                    }

                    PlayStatus.Error -> {
                        mBinding.includedLandscapeCtLoading.ctLoading.visibility = View.GONE
                        mBinding.includedLandscapeCtError.ctError.visibility = View.VISIBLE
                    }
                }
            }

        }
    }

    override fun onStop() {
//        "onStop".logd(TAG)
        super.onStop()

        if (mBackPressed || !mBinding.videoView.isBackgroundPlayEnabled) {
            mBinding.videoView.stopPlayback()
            mBinding.videoView.release(true)
            mBinding.videoView.stopBackgroundPlay()
        } else {
            mBinding.videoView.enterBackground()
        }
    }

    override fun onResume() {
//        "onResume".logd(TAG)
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        //使用横屏时到宽高
        AutoSizeConfig.getInstance().setDesignWidthInDp(LANDSCAPE_WIDTH)
        AutoSizeConfig.getInstance().setDesignHeightInDp(LANDSCAPE_HEIGHT)
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.hideStatusBar = true
        setStatusBar(StatusBarConfig)

    }

    override fun onPause() {
//        "onPause".logd(TAG)
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        //恢复竖屏，宽高也要回到竖屏时到宽高
        AutoSizeConfig.getInstance().setDesignWidthInDp(PORTRAIT_WIDTH)
        AutoSizeConfig.getInstance().setDesignHeightInDp(PORTRAIT_HEIGHT)
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarConfig.hideStatusBar = false
        setStatusBar(StatusBarConfig)
        destroyPlayer()
    }

    private fun destroyPlayer() {
        mBinding.videoView.stopPlayback()
        mBinding.videoView.release(true)
        mBinding.videoView.stopBackgroundPlay()
    }


//    override fun onBackPressed() {
//        mBackPressed = true
//        super.onBackPressed()
//    }

    /**
     * 跳转到联赛赛程页
     */
    private fun jumpToLeagueFragment() {
        navigate(LiveVideoLandscapeFragmentDirections.actionLiveVideoLandscapeFragmentToLeagueFragment())
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

    companion object {
        const val ANIMATION_DURATION = 300L

        const val LANDSCAPE_WIDTH = 812
        const val LANDSCAPE_HEIGHT = 375

        const val PORTRAIT_WIDTH = 375
        const val PORTRAIT_HEIGHT = 812
    }

}