package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipReserveBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipReserveViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass
//注单预约
class LiveBetSlipReserveFragment :
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipReserveBinding>(){
    override val vbClass: KClass<FragmentLiveBetslipReserveBinding> = FragmentLiveBetslipReserveBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}