package com.walisport.module.live.ui

import android.animation.Animator
import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout.GONE
import androidx.constraintlayout.widget.ConstraintLayout.VISIBLE
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.database.entity.MatchBasicInfoBean.MatchStatus
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import arch.cayenne.lib.qyplayer.GlobalConfig
import arch.cayenne.lib.qyplayer.transformFromPlayerConfig
import arch.cayenne.lib.qyplayer.transformToPlayerConfig
import arch.cayenne.lib.qyplayer.ui.widget.LivePlayerView
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.BUTTONS_ANIMATION_DURATION
import com.walisport.module.live.data.constants.VideoAnimatorConstants.Companion.HIDE_BUTTONS_TIMER
import com.walisport.module.live.databinding.FragmentLiveVideoPlayerBinding
import com.walisport.module.live.ui.popup.VideoResolutionHelper
import com.walisport.module.live.ui.video.PlayerViewCache
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveMatchMediaViewModel
import com.walisport.module.live.ui.viewmodel.LiveVideoPlayerViewModel
import com.xxx.qyplayer.DecryptMode
import com.xxx.qyplayer.PlayerMode
import com.xxx.qyplayer.PlayerState
import com.xxx.qyplayer.transformToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


/**
 * 竖屏播放视频页， 用在直播详情的首页
 */
class LiveVideoPlayerFragment :
    BaseFragment<LiveVideoPlayerViewModel, FragmentLiveVideoPlayerBinding>() {
    override val vbClass: KClass<FragmentLiveVideoPlayerBinding> =
        FragmentLiveVideoPlayerBinding::class
    override val vmClass: KClass<LiveVideoPlayerViewModel> = LiveVideoPlayerViewModel::class

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    private val mediaViewModel: LiveMatchMediaViewModel by sharedViewModel<LiveMatchMediaViewModel, LiveMatchMediaFragment>()


    private lateinit var videoView: LivePlayerView

    private lateinit var audioManager: AudioManager
    private var volumeObserver: VolumeObserver? = null

    private var buttonsDisplaying = true

    /**
     * 隐藏操作栏的定时Job
     */
    private var scheduledHideButtonsJob: Job? = null


//    /**
//     * 视频加载时的动画
//     */
//    private var loadingAnim: ObjectAnimator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 AudioManager
        audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // 初始化 VolumeObserver
        volumeObserver = VolumeObserver(Handler(Looper.getMainLooper()))
    }

    override fun onStart() {
        super.onStart()
        // 注册 ContentObserver
        volumeObserver?.let {
            requireContext().contentResolver.registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                it
            )
        }
    }

    override fun onStop() {
        super.onStop()
        // 注销 ContentObserver
        volumeObserver?.let {
            requireContext().contentResolver.unregisterContentObserver(it)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel

        initVideoView()
        scheduleHideButtons()
    }


    private fun initVideoView() {
        acquireVideoView()
        attachVideoView()
    }

    private fun acquireVideoView() {
        videoView = PlayerViewCache.acquirePlayerView {
            LivePlayerView(requireActivity()).apply {
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
    }

    private fun attachVideoView() {
        //单击事件处理
        videoView.setOnSingleTapListener {
            //单击事件
            if (buttonsDisplaying) {
                buttonsDisplaying = false

                hideButtonsAnimated()
            } else {
                buttonsDisplaying = true

                showButtonsAnimated()
            }
        }

        //播放状态处理
        videoView.setPlayerStateListener {
            mViewModel.setPlayerState(it)
        }

        videoView.setOnFirstFrameReceivedListener {
            lifecycleScope.launch {
                mBinding.root.startSafeAnimateSet(
                    {
                        playTogether(
                            mBinding.videoViewContainer.startSafeObjectAnimator(
                                "alpha",
                                mBinding.videoViewContainer.alpha,
                                1f
                            )
                        )
                    },
                    duration = 200,
                    interpolator = DecelerateInterpolator(),
                    start = true
                )
            }

        }

        videoView.setOnBackListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if (videoView.parent != null) {
            (videoView.parent as ViewGroup).removeView(videoView)
        }

        // 创建 LayoutParams，设置宽度和高度为 match_parent
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // 宽度
            LinearLayout.LayoutParams.MATCH_PARENT  // 高度
        )

        // 将 LayoutParams 应用到 VideoView
        videoView.layoutParams = layoutParams

        mBinding.videoViewContainer.addView(videoView)
    }


    override fun initListener() {

        with(mBinding) {

            ivToFullscreen.addScaleOnTouchAnimation()
            ivToFullscreen.clickNoRepeat {
                mBinding.videoViewContainer.removeAllViews()
                navigate(
                    LiveMainFragmentDirections.actionLiveMainFragmentToVideoLandscapeFragment()
                        .apply { arguments.putLong("matchId", mViewModel.matchId()) })
            }

            ivSoundToggle.addScaleOnTouchAnimation()
            ivSoundToggle.clickNoRepeat {
                scheduleHideButtons()
                mViewModel.changeMuteStatus()
            }

            tvVideoResolution.addScaleOnTouchAnimation()
            tvVideoResolution.clickNoRepeat {
                showVideoResolutionPopUp()
            }


        }

    }

    override suspend fun createObserver() {
        //监听比赛id变化
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.setMatchId(it)
            mViewModel.createObserver()
        }

        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {
                    if (it.source.isEmpty()) {
                        onDataSourceEmpty()
                    } else {

                        mBinding.ivToFullscreen.visibility = View.VISIBLE
                        val streamInfoBean =
                            it.source.firstOrNull { ele -> ele.isPlaying }?.liveStreams?.firstOrNull { ele -> ele.selected }

                        mBinding.tvVideoResolution.text = streamInfoBean?.streamType
                        val playUrl =
                            streamInfoBean
                                ?.playUrl()
                        playUrl?.takeIf { url -> url.isNotEmpty() }?.let { url ->
//                            "url:${url}, dataSource:${videoView.getDataSource()}".logd("videoCache")
                            if (url != videoView.getDataSource()) {
                                videoView.setDataSource(url)
                                videoView.prepare()
                            }
                        }

                    }

                }
            }

            mutedData().observe(viewLifecycleOwner) {
                mBinding.ivSoundToggle.setImageResource(
                    if (it) R.drawable.ic_muted else R.drawable.ic_immuted
                )

                videoView.setMute(it)
            }

            //监听横屏播放fragment销毁事件
            landscapeVideoFragmentDestroyedEvent().observe(viewLifecycleOwner) {
                attachVideoView()
                videoView.onResume()
            }

            //比赛状态的监听
            matchBeanLiveData.observe(viewLifecycleOwner) {
                it?.let { matchBean ->
                    val matchStatus =
                        MatchStatus.entries.find { status -> status.code == matchBean.basicInfo.status }

                    matchStatus?.let { _ ->
                        when (matchStatus) {
                            MatchStatus.IN_PROGRESS -> {
                                //比赛正在进行中才会拉取视频流
                                mViewModel.queryLiveStream()
                            }

                            else -> {

                            }
                        }

                    }

                }

            }



            playerState.observe(viewLifecycleOwner) {
                onPlayerStateReceived(it)
            }

            animationLiveUrl.observe(viewLifecycleOwner) {

            }

        }


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }


    override fun onPause() {
        super.onPause()
        if (videoView.parent == mBinding.videoViewContainer) {
            videoView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        videoView.onResume()
    }

    override fun onDestroyView() {
//        "portrait.onDestroyView".logd("videoCache")
        super.onDestroyView()
        PlayerViewCache.releasePlayerView(videoView) {
            it.onDestroy()
        }
    }


    /**
     * 隐藏底部操作栏
     */
    private fun hideButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_operate_area_height).toFloat()

        mBinding.root.startSafeAnimateSet(
            {
                playTogether(
                    mBinding.bottomArea.startSafeObjectAnimator(
                        "translationY",
                        *floatArrayOf(0f, operateAreaHeight)
                    ),
                    mBinding.bottomArea.startSafeObjectAnimator("alpha", 1f, 0.5f)
                )
            },
            duration = BUTTONS_ANIMATION_DURATION,
            interpolator = LinearInterpolator(),
            start = true
        )
    }

    /**
     * 展示底部操作栏
     */
    private fun showButtonsAnimated() {
        val operateAreaHeight =
            resources.getDimensionPixelSize(R.dimen.video_operate_area_height).toFloat()

        mBinding.root.startSafeAnimateSet(
            {
                playTogether(
                    mBinding.bottomArea.startSafeObjectAnimator(
                        "translationY",
                        *floatArrayOf(operateAreaHeight, 0f)
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
            },
            duration = BUTTONS_ANIMATION_DURATION,
            start = true
        )
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




    private fun onPlayerStateReceived(state: PlayerState) {

        when (state) {
            PlayerState.PLAYING -> {
//                loadingAnim?.cancel()
//                mBinding.ctLoading.visibility = GONE
                mBinding.ctError.visibility = GONE
            }

            PlayerState.PAUSED -> {
                //没有暂停按钮，
            }

            PlayerState.CONNECTING -> {
                // 创建旋转动画
//                loadingAnim = mBinding.ivVideoLoading.startSafeObjectAnimator(
//                    "rotation",  // 属性名称
//                    0f, 360f // 从 0 度旋转到 360 度
//                ).run {
//                    // 设置动画属性
//                    setDuration(1500) // 持续时间 1.5 秒
//                    repeatCount = ObjectAnimator.INFINITE // 无限循环
//                    interpolator = LinearInterpolator() // 匀速旋转
//
//                    // 启动动画
//                    start()
//                    this
//                }

                mBinding.ctError.visibility = GONE

            }

            PlayerState.ERROR -> {
                mBinding.ctError.visibility = VISIBLE
                mBinding.tvErrorTips.text = getString(R.string.live_video_error)
            }

            PlayerState.STOPPED -> {
//                loadingAnim?.cancel()
                mBinding.ctError.visibility = GONE
            }

            else -> {
//                loadingAnim?.cancel()
                mBinding.ctError.visibility = GONE
            }
        }

    }

    /**
     * 数据源为空
     */
    private fun onDataSourceEmpty() {
        if (!mViewModel.animationLiveUrl.value.isNullOrBlank()) {
            mediaViewModel.switchToAnimation()
        } else {
            mediaViewModel.switchToMatchStatus()
//            mBinding.ctLoading.visibility = GONE
//            mBinding.ctError.visibility = VISIBLE
//            mBinding.tvErrorTips.text = getString(R.string.no_live_stream)
//            mBinding.ivChooseSource.visibility = View.INVISIBLE
//            mBinding.ivToFullscreen.visibility = View.INVISIBLE
        }
    }

    private fun showVideoResolutionPopUp() {
        scheduledHideButtonsJob?.cancel()

        val helper = VideoResolutionHelper()

        lifecycleScope.launch(Dispatchers.IO) {
            val beanList = mViewModel.getVideoResolutionList()

            if (beanList != null) {
                lifecycleScope.launch {
                    helper.showPopUp(mBinding.tvVideoResolution, beanList) {
                        scheduleHideButtons()

                        mViewModel.changeResolution(it)

                        if (mBinding.videoViewContainer.alpha != 0f) {
                            mBinding.root.startSafeAnimateSet(
                                {
                                    playTogether(
                                        mBinding.videoViewContainer.startSafeObjectAnimator(
                                            "alpha",
                                            mBinding.videoViewContainer.alpha,
                                            0f
                                        )
                                    )
                                },
                                duration = 200,
                                interpolator = DecelerateInterpolator(),
                                start = true
                            )
                        }
                    }
                }
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
        const val TAG = "LiveVideoPlayerFragment"
    }


}