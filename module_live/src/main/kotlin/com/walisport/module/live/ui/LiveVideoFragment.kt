package com.walisport.module.live.ui

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ViewUtils.getStatusBarHeight
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimension
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.PlayStatus
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import kotlin.reflect.KClass


/**
 * 竖屏播放视频页， 用在直播详情的首页
 */
class LiveVideoFragment : BaseFragment<LiveVideoViewModel, FragmentLiveVideoBinding>() {
    override val vbClass: KClass<FragmentLiveVideoBinding> = FragmentLiveVideoBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    private val playingStatusLiveData: MutableLiveData<PlayStatus> =
        MutableLiveData(PlayStatus.Loading)

    private var loadingAnim: ObjectAnimator? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var bufferingTimeoutJob: Job? = null


    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel
        mBinding.includedMatchNotInProgress.model = mViewModel

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

        with(mBinding) {
            ivChooseSource.setOnClickListener {
                val location = IntArray(2)
                videoView.getLocationOnScreen(location)
                val x = location[0]
                val y =
                    location[1] + videoView.measuredHeight - getStatusBarHeight(requireContext())
                LiveVideoSourcePortraitFragment().apply {
                    arguments = Bundle().apply {
                        putLong("matchId", mViewModel.matchId())
                        putInt(
                            arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment.POSITION_X,
                            x
                        )
                        putInt(
                            arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment.POSITION_Y,
                            y
                        )
                        putInt(
                            arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment.WIDTH,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        putInt(
                            arch.cayenne.lib.base.ui.fragment.LocationFixedDialogFragment.HEIGHT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                    show(this@LiveVideoFragment.childFragmentManager)
                }

            }

            ivToFullscreen.clickNoRepeat {
                destroyPlayer()
                navigate(
                    LiveMainFragmentDirections.actionLiveMainFragmentToVideoLandscapeFragment()
                        .apply { arguments.putLong("matchId", mViewModel.matchId()) })
            }

            ivSoundToggle.clickNoRepeat { mViewModel.changeMuteStatus() }
        }

    }

    override fun createObserver() {
        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {

                    val playUrl = it.source.firstOrNull { ele -> ele.isPlaying }?.playUrl()
                    playUrl?.takeIf { url -> url.isNotEmpty() }?.let { url ->
                        mBinding.videoView.setVideoURI(Uri.parse(url))
                        mBinding.videoView.start()
                    }

                }
            }

            mutedData().observe(viewLifecycleOwner) {
                mBinding.ivSoundToggle.setImageResource(
                    if (it) R.drawable.shape_muted else R.drawable.shape_immuted
                )

                if (mBinding.videoView.isPlaying) {
                    mBinding.videoView.mediaPlayer.setVolume(if (it) 0f else 1f, if (it) 0f else 1f)
                }
            }

            //比赛状态的监听
            matchBeanLiveData.observe(viewLifecycleOwner) {
                it?.let { matchBean ->
                    val matchStatus =
                        MatchStatus.entries.find { status -> status.code == matchBean.basicInfo.status }

                    matchStatus?.let { _ ->
                        when (matchStatus) {
                            MatchStatus.IN_PROGRESS -> {
                                //比赛正在进行中
                                mBinding.ctVideoPlay.visibility = View.VISIBLE
                                //比赛正在进行中才会拉取视频流
                                mViewModel.queryLiveStream()
                            }

                            else -> {
                                //其他情况
                                mBinding.ctVideoPlay.visibility = View.GONE
                            }
                        }

                    }

                }

            }

            homeTeamName.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.includedMatchNotInProgress.tvHomeTeam.text = it
                }
            }

            homeTeamIcon.observe(viewLifecycleOwner) {
                it?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .placeholder(arch.cayenne.lib.res.R.color.color_333A45)
                        .error(arch.cayenne.lib.res.R.color.color_333A45)
                        .into(mBinding.includedMatchNotInProgress.ivHomeTeam)
                }
            }

            awayTeamName.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvAwayTeam.text = it }
            }

            awayTeamIcon.observe(viewLifecycleOwner) {
                it?.let {
                    Glide.with(requireContext())
                        .load(it)
                        .placeholder(arch.cayenne.lib.res.R.color.color_333A45)
                        .error(arch.cayenne.lib.res.R.color.color_333A45)
                        .into(mBinding.includedMatchNotInProgress.ivAwayTeam)
                }
            }

            titleText.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvTitle.text = it }
            }

            titleTextSize.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.includedMatchNotInProgress.tvTitle.setTextSize(
                        COMPLEX_UNIT_PX,
                        it.getDimension()
                    )
                }
            }

            titleTextColor.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvTitle.setTextColor(it.getColor()) }
            }

            subTitleText.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvSubtitle.text = it }
            }

            subTitleTextSize.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.includedMatchNotInProgress.tvSubtitle.setTextSize(
                        COMPLEX_UNIT_PX,
                        it.getDimension()
                    )
                }
            }
            subTitleTextColor.observe(viewLifecycleOwner) {
                it?.let { mBinding.includedMatchNotInProgress.tvSubtitle.setTextColor(it.getColor()) }
            }


        }

        //播放状态的监听
        playingStatusLiveData.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    PlayStatus.Playing -> {
                        loadingAnim?.cancel()

                        mBinding.includedCtLoading.ctLoading.visibility = View.GONE
                        mBinding.includedCtError.ctError.visibility = View.GONE
                    }

                    PlayStatus.Loading -> {
                        // 创建旋转动画
                        loadingAnim = ObjectAnimator.ofFloat(
                            mBinding.includedCtLoading.ivVideoLoading,  // 目标 View
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

                        mBinding.includedCtLoading.ctLoading.visibility = View.VISIBLE
                        mBinding.includedCtError.ctError.visibility = View.GONE
                    }

                    PlayStatus.Error -> {
                        mBinding.includedCtLoading.ctLoading.visibility = View.GONE
                        mBinding.includedCtError.ctError.visibility = View.VISIBLE
                    }
                }
            }

        }

        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }


    override fun initData() {
        super.initData()
    }

    override fun onPause() {
        super.onPause()
        mBinding.videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!mBinding.videoView.isPlaying) {
            mBinding.videoView.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    private fun destroyPlayer() {
        mBinding.videoView.stopPlayback()
        mBinding.videoView.release(true)
        mBinding.videoView.stopBackgroundPlay()
    }


    companion object {
        const val TAG = "LiveVideoFragment"
    }
}