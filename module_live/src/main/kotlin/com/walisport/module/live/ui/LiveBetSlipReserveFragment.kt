package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipReserveBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipReserveViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class LiveBetSlipReserveFragment :BaseFragment<LiveBetSlipReserveViewModel,FragmentLiveBetslipReserveBinding>(){
    override val vbClass: KClass<FragmentLiveBetslipReserveBinding> = FragmentLiveBetslipReserveBinding::class
    override val vmClass: KClass<LiveBetSlipReserveViewModel> = LiveBetSlipReserveViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}