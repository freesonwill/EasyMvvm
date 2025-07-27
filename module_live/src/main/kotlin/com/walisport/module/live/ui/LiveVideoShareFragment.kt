package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveShareBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoPlayerViewModel
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

/**
 * 视频横屏播放时的分享页
 */
class LiveVideoShareFragment : BaseFragment<LiveVideoPlayerViewModel, FragmentLiveShareBinding>() , CancelAdapt{
    override val vbClass: KClass<FragmentLiveShareBinding> = FragmentLiveShareBinding::class
    override val vmClass: KClass<LiveVideoPlayerViewModel> = LiveVideoPlayerViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.operateShare.addScaleOnTouchAnimation()
        mBinding.operateShare.clickNoRepeat {
            showToast(getString(R.string.not_implemented))
        }
    }

    override fun createObserver() {

    }

    override fun onResume() {
        super.onResume()
        mBinding.root.fitsSystemWindows = false
    }


    companion object {
        const val TAG = "LiveVideoShareFragment"
    }

}