package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import com.walisport.module.live.databinding.FragmentLiveOutsBinding
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import kotlin.reflect.KClass

/**
 * 赛况Tab页
 */

class LiveOutsFragment : BaseFragment<LiveOutsViewModel, FragmentLiveOutsBinding>() {

    override val vbClass: KClass<FragmentLiveOutsBinding> = FragmentLiveOutsBinding::class
    override val vmClass: KClass<LiveOutsViewModel> = LiveOutsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.getMatchTrendData(458436)
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.liveOutsData.observe(this) {
            if (it != null) {
                mBinding.viewTechStatic.setTrendData(it)
            }
        }
    }
}