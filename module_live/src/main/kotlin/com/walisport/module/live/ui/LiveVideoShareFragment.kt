package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.databinding.FragmentLiveShareBinding
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import kotlin.reflect.KClass

class LiveVideoShareFragment : BaseFragment<LiveVideoViewModel, FragmentLiveShareBinding>() {
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
//        //使用横屏时到宽高
//        AutoSizeConfig.getInstance().setDesignWidthInDp(LANDSCAPE_WIDTH)
//        AutoSizeConfig.getInstance().setDesignHeightInDp(LANDSCAPE_HEIGHT)
    }

    override fun onPause() {
        super.onPause()
//        //恢复竖屏，宽高也要回到竖屏时到宽高
//        AutoSizeConfig.getInstance().setDesignWidthInDp(PORTRAIT_WIDTH)
//        AutoSizeConfig.getInstance().setDesignHeightInDp(PORTRAIT_HEIGHT)
    }

    companion object {
        const val TAG = "LiveVideoShareFragment"
    }

}