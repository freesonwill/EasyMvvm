package com.walisport.module.home.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
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