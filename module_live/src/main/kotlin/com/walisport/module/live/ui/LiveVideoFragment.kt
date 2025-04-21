package com.walisport.module.live.ui

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.LocationFixedDialogFragment
import arch.cayenne.lib.common.utils.ViewUtils.getStatusBarHeight
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.R
import com.walisport.module.live.data.PlayStatus
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import tv.danmaku.ijk.media.player.IMediaPlayer
import kotlin.reflect.KClass


/**
 * 竖屏播放视频页， 用在直播详情的首页
 */
class LiveVideoFragment : BaseFragment<LiveVideoViewModel, FragmentLiveVideoBinding>() {
    override val vbClass: KClass<FragmentLiveVideoBinding> = FragmentLiveVideoBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    private val playingStatusLiveData: MutableLiveData<PlayStatus> =
        MutableLiveData(PlayStatus.Playing)

    private var loadingAnim: ObjectAnimator? = null

    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.queryLiveStream(matchId)

        mBinding.videoView.setOnInfoListener { mp, what, extra ->
            when (what) {
                IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    // 视频开始缓冲（加载中）
                    //  LogUtils.i(TAG, "Buffering started")
                    playingStatusLiveData.postValue(PlayStatus.Loading)
                }

                IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    // 视频缓冲结束（加载完成，可以播放）
                    //  LogUtils.i(TAG, "Buffering ended")
                    playingStatusLiveData.postValue(PlayStatus.Playing)
                }

                IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    // 视频开始渲染（第一帧显示）
                    //  LogUtils.i(TAG, "Video rendering started")
                    playingStatusLiveData.postValue(PlayStatus.Playing)
                }

                IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START -> {
                    // 音频开始渲染
                    //  LogUtils.i(TAG, "Audio rendering started")
                }
            }

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
                        putInt(LocationFixedDialogFragment.POSITION_X, x)
                        putInt(LocationFixedDialogFragment.POSITION_Y, y)
                        putInt(
                            LocationFixedDialogFragment.WIDTH,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        putInt(
                            LocationFixedDialogFragment.HEIGHT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                    show(this@LiveVideoFragment.childFragmentManager)
                }

            }
            ivToFullscreen.clickNoRepeat {
                destroyPlayer()
                navigate(LiveMainFragmentDirections.actionLiveMainFragmentToVideoLandscapeFragment())
            }

            ivSoundToggle.clickNoRepeat { mViewModel.changeMuteStatus() }
        }

    }

    override fun createObserver() {
        with(mViewModel) {
            liveVideoBean.observe(viewLifecycleOwner) {
                it?.let {
                    mBinding.videoView.setVideoURI(Uri.parse(it.playUrl()))
                    mBinding.videoView.start()
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
        }

        playingStatusLiveData.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    PlayStatus.Playing -> {
                        loadingAnim?.cancel()
                        mBinding.ctLoading.visibility = View.GONE
                    }

                    PlayStatus.Loading -> {
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

                        mBinding.ctLoading.visibility = View.VISIBLE
                    }

                    PlayStatus.Error -> {}
                }
            }

        }

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