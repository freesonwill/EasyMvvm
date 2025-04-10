package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.live.data.model.GoalTrendBean
import com.walisport.module.live.data.model.MatchEventBean
import com.walisport.module.live.databinding.FragmentLiveOutsBinding
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import kotlin.reflect.KClass

/**
 * 赛况Tab
 */

class LiveOutsFragment : BaseFragment<LiveOutsViewModel, FragmentLiveOutsBinding>() {

    override val vbClass: KClass<FragmentLiveOutsBinding> = FragmentLiveOutsBinding::class
    override val vmClass: KClass<LiveOutsViewModel> = LiveOutsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.viewTechStatic.setTeamName("法国", "阿根廷")
        mBinding.viewTechStatic.setScore("2:2")
        mBinding.viewTechStatic.setAttackData(8, 5)
        mBinding.viewTechStatic.setDangerAttackData(10, 12)
        mBinding.viewTechStatic.setBallControlData(20, 13)
        mBinding.viewTechStatic.setHomeAwayData(3, 4, 5, 3, 2, 4)
        mBinding.viewTechStatic.setProgressData()
        mBinding.viewTechEvent.setTeamName("法国", "阿根廷")

        mViewModel.setGoalTrendData()
        mViewModel.setMatchEventData()
    }

    override fun initListener() {

    }

    override fun createObserver() {
        mViewModel.matchEventList.observe(viewLifecycleOwner) {
            val list = it as ArrayList<MatchEventBean>
            mBinding.viewTechEvent.setData(list)
        }
        mViewModel.goalTrendList.observe(viewLifecycleOwner) {
            val list = it as ArrayList<GoalTrendBean>
            mBinding.viewTechStatic.setData(list)
        }
    }
}