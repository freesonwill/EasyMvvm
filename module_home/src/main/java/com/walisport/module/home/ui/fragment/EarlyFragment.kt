package com.walisport.module_home.ui.fragment

import android.os.Bundle
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.home.databinding.FragmentEarlyBinding
import kotlin.reflect.KClass

class EarlyFragment: BaseFragment<EmptyViewModel, FragmentEarlyBinding>(){
    override val vbClass: KClass<FragmentEarlyBinding> = FragmentEarlyBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}