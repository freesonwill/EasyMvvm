package com.walisport.module.hall.ui.adapter

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import com.walisport.module.hall.databinding.FragmentGameAllRankingListBinding
import kotlin.reflect.KClass

class GameAllRankingListFragment : BaseFragment<EmptyViewModel, FragmentGameAllRankingListBinding>() {
    companion object {
        fun newInstance() = GameAllRankingListFragment()
    }

    override val vbClass: KClass<FragmentGameAllRankingListBinding> = FragmentGameAllRankingListBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class


    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }


}