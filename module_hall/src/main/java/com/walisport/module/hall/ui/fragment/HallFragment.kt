package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import com.walisport.module.hall.databinding.FragmentHallBinding
import com.walisport.module.hall.ui.viewmodel.HallViewModel
import kotlin.reflect.KClass

/**
 * 游戏大厅界面
 */

class HallFragment : BaseFragment<HallViewModel, FragmentHallBinding>() {

    override val vbClass: KClass<FragmentHallBinding> = FragmentHallBinding::class
    override val vmClass: KClass<HallViewModel> = HallViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

        mBinding.root.touchBackPressed()

        mBinding.balanceView.init(childFragmentManager)
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