package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.databinding.FragmentLiveShareBinding
import com.walisport.module.live.ui.LiveVideoLandscapeFragment.Companion.LANDSCAPE_HEIGHT
import com.walisport.module.live.ui.LiveVideoLandscapeFragment.Companion.LANDSCAPE_WIDTH
import com.walisport.module.live.ui.LiveVideoLandscapeFragment.Companion.PORTRAIT_HEIGHT
import com.walisport.module.live.ui.LiveVideoLandscapeFragment.Companion.PORTRAIT_WIDTH
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.internal.CancelAdapt
import kotlin.reflect.KClass

class LiveVideoChooseSourceFragment : BaseFragment<LiveVideoViewModel, FragmentLiveShareBinding>() , CancelAdapt{
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
        const val TAG = "LiveVideoChooseSourceFragment"
    }

}