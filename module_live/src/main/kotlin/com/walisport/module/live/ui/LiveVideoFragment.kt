package com.walisport.module.live.ui

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.LocationFixedDialogFragment
import arch.cayenne.lib.common.utils.ViewUtils.getStatusBarHeight
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import kotlin.reflect.KClass


/**
 * 竖屏播放视频页， 用在直播详情的首页
 */
class LiveVideoFragment : BaseFragment<LiveVideoViewModel, FragmentLiveVideoBinding>() {
    override val vbClass: KClass<FragmentLiveVideoBinding> = FragmentLiveVideoBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val matchId = arguments?.getLong("matchId") ?: 0
        mViewModel.queryLiveStream(matchId)
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