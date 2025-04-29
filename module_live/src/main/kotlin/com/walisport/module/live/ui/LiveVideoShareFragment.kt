package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.databinding.FragmentLiveShareBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

/**
 * 视频横屏播放时的分享页
 */
class LiveVideoShareFragment : BaseFragment<LiveVideoViewModel, FragmentLiveShareBinding>() , CancelAdapt{
    override val vbClass: KClass<FragmentLiveShareBinding> = FragmentLiveShareBinding::class
    override val vmClass: KClass<LiveVideoViewModel> = LiveVideoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.operateShare.clickNoRepeat {
            "operateShare clicked".logd(TAG)
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