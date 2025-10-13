package com.walisport.module.misc.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.misc.databinding.FragmentSecondBinding
import com.walisport.module.misc.ui.viewmodel.MiscViewModel
import kotlin.reflect.KClass

/**
 * 我的界面
 */

class SecondFragment : BaseFragment<MiscViewModel, FragmentSecondBinding>() {

    override val vbClass: KClass<FragmentSecondBinding> = FragmentSecondBinding::class
    override val vmClass: KClass<MiscViewModel> = MiscViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {
        mBinding.ivBack.clickNoRepeat {
            findNavController().navigateUp()
        }
    }

    override suspend fun createObserver() {

    }

    override fun onStart() {
        super.onStart()
        mBinding.root.fitsSystemWindows = false
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }

}