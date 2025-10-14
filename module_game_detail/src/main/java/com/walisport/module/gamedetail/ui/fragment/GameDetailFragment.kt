package com.walisport.module.gamedetail.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.gamedetail.databinding.FragmentGameDetailBinding
import com.walisport.module.gamedetail.ui.viewmodel.GameDetailViewModel
import kotlin.reflect.KClass

class GameDetailFragment: BaseFragment<GameDetailViewModel, FragmentGameDetailBinding>() {
    override val vbClass: KClass<FragmentGameDetailBinding> = FragmentGameDetailBinding::class
    override val vmClass: KClass<GameDetailViewModel> = GameDetailViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun initListener() {
        TODO("Not yet implemented")
    }

    override suspend fun createObserver() {
        TODO("Not yet implemented")
    }
}