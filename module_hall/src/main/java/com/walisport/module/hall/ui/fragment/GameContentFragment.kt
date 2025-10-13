package com.walisport.module.hall.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.walisport.module.hall.databinding.FragmentGameContentBinding
import kotlin.reflect.KClass

class GameContentFragment : BaseFragment<EmptyViewModel, FragmentGameContentBinding>() {
    companion object {
        fun newInstance() = GameContentFragment()
    }
    override val vbClass: KClass<FragmentGameContentBinding> = FragmentGameContentBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}