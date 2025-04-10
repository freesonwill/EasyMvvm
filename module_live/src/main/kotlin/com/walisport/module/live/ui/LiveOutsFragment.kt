package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.live.data.model.GoalTrendBean
import com.walisport.module.live.databinding.FragmentLiveOutsBinding
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import kotlin.reflect.KClass

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
        val array = ArrayList<GoalTrendBean>()
        val temp1 = GoalTrendBean(0, 10000101, true, 1, 2, 70)
        val temp2 = GoalTrendBean(0, 10000101, false, 2, 4, 60)
        val temp3 = GoalTrendBean(0, 10000101, true, 3, 10, 20)
        val temp4 = GoalTrendBean(0, 10000101, false, 3, 15, 10)
        val temp5 = GoalTrendBean(0, 10000101, true, 4, 20, 100)
        val temp6 = GoalTrendBean(0, 10000101, false, 1, 22, 70)
        val temp7 = GoalTrendBean(0, 10000101, false, 2, 26, 60)
        val temp8 = GoalTrendBean(0, 10000101, true, 4, 30, 20)
        val temp9 = GoalTrendBean(0, 10000101, false, 3, 40, 100)
        val temp10 = GoalTrendBean(0, 10000101, false, 3, 75, 80)
        array.add(temp1)
        array.add(temp2)
        array.add(temp3)
        array.add(temp4)
        array.add(temp5)
        array.add(temp6)
        array.add(temp7)
        array.add(temp8)
        array.add(temp9)
        array.add(temp10)
        mBinding.viewTechStatic.setData(array)
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }


}