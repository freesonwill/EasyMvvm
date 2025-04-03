package com.walisport.module.live.ui

import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.module.live.databinding.FragmentLiveVideoBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveVideoFragment : BaseFragment<LiveVideoViewModel, FragmentLiveVideoBinding>() {
    override val mBinding: FragmentLiveVideoBinding by viewBind()
    override val mViewModel: LiveVideoViewModel by viewModel()


    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.ivChooseSource.setOnClickListener {

        }
        mBinding.ivToFullscreen.setOnClickListener {
            findNavController().navigate(Uri.parse("walisport://video_landscape_activity?userId=lucy"))
        }
    }

    override fun createObserver() {
        mViewModel.liveUrl.observe(viewLifecycleOwner) {
            mBinding.videoView.setVideoURI(Uri.parse(it))
            mBinding.videoView.start()
            "videoView.start".logd(TAG)

        }

    }

    override fun onPause() {
        "onPause".logd(TAG)
        super.onPause()
        mBinding.videoView.pause()
    }

    override fun onResume() {
        "onResume".logd(TAG)
        super.onResume()
        mBinding.videoView.resume()
    }

    override fun onDestroy() {
        "onDestroy".logd(TAG)
        super.onDestroy()
        mBinding.videoView.release(true)
    }

    companion object {
        const val TAG = "LiveVideoFragment"
    }
}