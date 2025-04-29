package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.widget.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.live.R
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
        val matchId = arguments?.getLong("matchId") ?: 0L
        mViewModel.getMatchTrendData(matchId)
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.liveOutsData.observe(this) {
            if (it != null) {
                mBinding.mainLayout.setVisibilityGone()
                mBinding.viewTechStatic.setTrendData(it)
            } else {
                mBinding.mainLayout.setState(
                    DynamicStateLayout.States.DATA_EMPTY,
                    R.string.outs_empty.getString()
                )
            }
        }
    }
}