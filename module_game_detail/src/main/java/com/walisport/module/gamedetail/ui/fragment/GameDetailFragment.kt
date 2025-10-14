package com.walisport.module.gamedetail.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.gamedetail.databinding.FragmentGameDetailBinding
import com.walisport.module.gamedetail.ui.viewmodel.GameDetailViewModel
import kotlin.reflect.KClass

class GameDetailFragment: BaseFragment<GameDetailViewModel, FragmentGameDetailBinding>() {
    override val vbClass: KClass<FragmentGameDetailBinding> = FragmentGameDetailBinding::class
    override val vmClass: KClass<GameDetailViewModel> = GameDetailViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            viewBalance.init(childFragmentManager)
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.FULLSCREEN
        setStatusBar(StatusBarConfig, mBinding.root)
    }
}