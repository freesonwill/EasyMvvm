package com.walisport.module.message.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.message.databinding.FragmentTodayMatchBinding
import com.walisport.module.message.ui.viewmodel.TodayMatchViewModel
import kotlin.reflect.KClass

/**
 * 今日赛事页
 */

class TodayMatchFragment : BaseFragment<TodayMatchViewModel, FragmentTodayMatchBinding>() {

    override val vbClass: KClass<FragmentTodayMatchBinding> = FragmentTodayMatchBinding::class
    override val vmClass: KClass<TodayMatchViewModel> = TodayMatchViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun initListener() {
        TODO("Not yet implemented")
    }

    override fun createObserver() {
        TODO("Not yet implemented")
    }

}