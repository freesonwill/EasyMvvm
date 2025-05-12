package com.walisport.module.live.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ViewUtils.getStatusBarHeight
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimension
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.qyplayer.GlobalConfig
import arch.cayenne.lib.qyplayer.transformFromPlayerConfig
import arch.cayenne.lib.qyplayer.transformToPlayerConfig
import com.bumptech.glide.Glide
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import com.xxx.qyplayer.PlayerMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.reflect.KClass


/**
 * 竖屏播放视频页， 用在直播详情的首页
 */
class LiveVideoFragment : BaseFragment<LiveVideoViewModel, FragmentLiveVideoBinding>() {
    override val vbClass: KClass<FragmentLiveVideoBinding> = FragmentLiveVideoBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class


    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var bufferingTimeoutJob: Job? = null

    private val mPlayerMode = PlayerMode.FLUENCY
    private lateinit var mGlobalConfig: GlobalConfig


    override fun initView(savedInstanceState: Bundle?) {
        mBinding.model = mViewModel
        mBinding.includedMatchNotInProgress.model = mViewModel

        mBinding.videoView.apply { init(mPlayerMode)
            keepScreenOn = true

        }

        initPlayer()

       //todo: 设置超时时间

//        val mediaPlayer = mBinding.videoView.mediaPlayer
//        if (mediaPlayer is IjkMediaPlayer) {
//            mediaPlayer.setOption(
//                IjkMediaPlayer.OPT_CATEGORY_FORMAT,
//                "timeout",
//                10000000
//            ); // 10秒总超时（微秒）
//            mediaPlayer
//                .setOption(
//                    IjkMediaPlayer.OPT_CATEGORY_FORMAT,
//                    "connect_timeout",
//                    5000
//                ); // 5秒连接超时（毫秒）
//            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 0); // 禁用自动重连
//        }


        //todo: 加载状态监听

//        mBinding.videoView.setOnInfoListener { mp, what, extra ->
//            when (what) {
//                IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
//                    // 视频开始缓冲（加载中）
////                    "Buffering started".logd(TAG)
//                    playingStatusLiveData.postValue(PlayStatus.Loading)
//
//                    // 启动协程，10秒超时
//                    bufferingTimeoutJob = coroutineScope.launch {
//                        delay(10000)
//                        playingStatusLiveData.postValue(PlayStatus.Error)
//                    }
//
//                }
//
//                IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
//                    // 视频缓冲结束（加载完成，可以播放）
////                    "Buffering ended".logd(TAG)
//                    playingStatusLiveData.postValue(PlayStatus.Playing)
//                    bufferingTimeoutJob?.cancel()
//
//                }
//
//                IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
//                    // 视频开始渲染（第一帧显示）
////                    "Video rendering started".logd(TAG)
//                    playingStatusLiveData.postValue(PlayStatus.Playing)
//                    bufferingTimeoutJob?.cancel()
//                }
//
//                IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START -> {
//                    // 音频开始渲染
//                    //  LogUtils.i(TAG, "Audio rendering started")
//                }
//
//                else -> {
//                    "player info. what:${what}".logd(TAG)
//                }
//            }
//
//            true
//        }


        //todo: 加载失败监听
//        mBinding.videoView.setOnErrorListener { mp, what, extra ->
//            when (what) {
//                IjkMediaPlayer.MEDIA_ERROR_IO -> {
//                    "Error: Network I/O error (MEDIA_ERROR_IO), extra: $extra".logd(TAG)
//                    playingStatusLiveData.postValue(PlayStatus.Error)
//                }
//
//                IjkMediaPlayer.MEDIA_ERROR_MALFORMED -> {
//                    "Error: Malformed stream (MEDIA_ERROR_MALFORMED), extra: $extra".logd(TAG)
//                    playingStatusLiveData.postValue(PlayStatus.Error)
//                }
//
//                IjkMediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
//                    "Error: Unsupported format (MEDIA_ERROR_UNSUPPORTED), extra: $extra".logd(TAG)
//                    playingStatusLiveData.postValue(PlayStatus.Error)
//                }
//
//                IjkMediaPlayer.MEDIA_ERROR_TIMED_OUT -> {
//                    "Error: Timeout (MEDIA_ERROR_TIMED_OUT), extra: $extra".logd(TAG)
//                    playingStatusLiveData.postValue(PlayStatus.Error)
//                }
//
//                else -> {
//                    "Unknown error, what: $what, extra: $extra".logd(TAG)
//                    playingStatusLiveData.postValue(PlayStatus.Error)
//                }
//            }
//            // 返回 true 表示错误已处理，false 表示未处理
//            true
//        }
    }

    private fun initPlayer() {
        mGlobalConfig = GlobalConfig(requireContext()).also {
            if (!it.inited) { // 首次启动从本地播放器获取默认配置
                it.transformFromPlayerConfig(mBinding.videoView.getConfig())

                // 更改底层默认配置。默认加密流，需要开启解密
                it.isAudioDecrypt = false
                it.isVideoDecrypt = false
                it.isHWDecode = false

                it.inited = true
            }
        }

        mBinding.videoView.run {
            setConfig(mGlobalConfig.transformToPlayerConfig())
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
                        "url:${url}".logd("LiveVideoFragment")
                        mBinding.videoView.setDataSource(url)
                        mBinding.videoView.prepare()
                    }

                }
            }

            mutedData().observe(viewLifecycleOwner) {
                mBinding.ivSoundToggle.setImageResource(
                    if (it) R.drawable.shape_muted else R.drawable.shape_immuted
                )

                mBinding.videoView.setMute(it)
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


        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.setMatchId(matchId)

        mViewModel.createObserver()
    }


    override fun initData() {
        super.initData()
    }

    override fun onPause() {
        super.onPause()
        mBinding.videoView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mBinding.videoView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.videoView.onDestroy()
    }

    override fun onDestroyView() {
        destroyPlayer()
        super.onDestroyView()
    }

    private fun destroyPlayer() {

        //todo: destroyPlayer
//        mBinding.videoView.stopPlayback()
//        mBinding.videoView.release(true)
//        mBinding.videoView.stopBackgroundPlay()
    }

    companion object {
        const val TAG = "LiveVideoFragment"
    }
}