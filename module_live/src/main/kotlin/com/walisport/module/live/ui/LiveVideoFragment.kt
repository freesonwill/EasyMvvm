package com.walisport.module.live.ui

import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class LiveVideoFragment : BaseFragment<LiveVideoViewModel, FragmentLiveVideoBinding>() {
    override val vbClass: KClass<FragmentLiveVideoBinding> = FragmentLiveVideoBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.ivChooseSource.setOnClickListener {

        }
        mBinding.ivToFullscreen.clickNoRepeat {
            destroyPlayer()

            findNavController().navigate(Uri.parse("walisport://video_landscape_activity?userId=lucy"))
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

    private fun destroyPlayer(){
        mBinding.videoView.stopPlayback()
        mBinding.videoView.release(true)
        mBinding.videoView.stopBackgroundPlay()
    }

    companion object {
        const val TAG = "LiveVideoFragment"
    }
}