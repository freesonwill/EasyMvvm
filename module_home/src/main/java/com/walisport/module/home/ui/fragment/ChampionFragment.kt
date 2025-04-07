package com.walisport.module_home.ui.fragment

import android.os.Bundle
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.module.home.databinding.FragmentChampionBinding
import kotlin.reflect.KClass

class ChampionFragment: BaseFragment<EmptyViewModel, FragmentChampionBinding>(){
    override val vbClass: KClass<FragmentChampionBinding> = FragmentChampionBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
//        override val mBinding: FragmentChampionBinding by viewBind()
//        override val mViewModel: EmptyViewModel by viewModel()
    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

}