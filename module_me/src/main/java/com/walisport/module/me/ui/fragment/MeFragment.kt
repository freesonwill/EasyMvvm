package com.walisport.module.me.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.me.databinding.FragmentMeBinding
import com.walisport.module.me.ui.viewmodel.MeViewModel
import kotlin.reflect.KClass

/**
 * 我的界面
 */

class MeFragment : BaseFragment<MeViewModel, FragmentMeBinding>() {

    override val vbClass: KClass<FragmentMeBinding> = FragmentMeBinding::class
    override val vmClass: KClass<MeViewModel> = MeViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.root.touchBackPressed()
    }

    override fun initListener() {

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