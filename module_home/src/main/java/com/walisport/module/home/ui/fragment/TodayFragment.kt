package com.walisport.module_home.ui.fragment

import android.os.Bundle
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.home.databinding.FragmentTodayBinding
import kotlin.reflect.KClass

class TodayFragment: BaseFragment<EmptyViewModel, FragmentTodayBinding>(){
    override val vbClass: KClass<FragmentTodayBinding> = FragmentTodayBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}