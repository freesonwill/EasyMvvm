package com.walisport.module.live.ui

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.PositionedDialogFragment
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ViewUtils.getStatusBarHeight
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
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
        mViewModel.addMockData()
    }

    override fun initListener() {
        mBinding.ivChooseSource.setOnClickListener {
            val location = IntArray(2)
            mBinding.videoView.getLocationOnScreen(location)
            val x = location[0]
            val y =
                location[1] + mBinding.videoView.measuredHeight - getStatusBarHeight(requireContext())
            LiveVideoSourcePortraitFragment().apply {
                arguments = Bundle().apply {
                    putInt(PositionedDialogFragment.POSITION_X, x)
                    putInt(PositionedDialogFragment.POSITION_Y, y)
                    putInt(PositionedDialogFragment.WIDTH, ViewGroup.LayoutParams.MATCH_PARENT)
                    putInt(PositionedDialogFragment.HEIGHT, ViewGroup.LayoutParams.WRAP_CONTENT)
                }
                show(this@LiveVideoFragment.childFragmentManager)
            }

        }
        mBinding.ivToFullscreen.clickNoRepeat {
            destroyPlayer()
            navigate(LiveMainFragmentDirections.actionLiveMainFragmentToVideoLandscapeFragment())
        }
    }

    override fun createObserver() {
        mViewModel.liveUrl.observe(viewLifecycleOwner) {
            mBinding.videoView.setVideoURI(Uri.parse(it))
            mBinding.videoView.start()
//            "videoView.start".logd(TAG)
        }

    }

    override fun onPause() {
//        "onPause".logd(TAG)
        super.onPause()
//        mBinding.videoView.pause()
    }

    override fun onResume() {
//        "onResume".logd(TAG)
        super.onResume()
        if (!mBinding.videoView.isPlaying) {
            mBinding.videoView.start()
        }
    }


    override fun onDestroy() {
        "onDestroy".logd(TAG)
        super.onDestroy()

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