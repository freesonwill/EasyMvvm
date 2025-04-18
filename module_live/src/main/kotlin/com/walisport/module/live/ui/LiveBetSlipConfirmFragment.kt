package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipConfirmBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipConfirmViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass
//注单确认
class LiveBetSlipConfirmFragment:
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipConfirmBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipConfirmBinding> = FragmentLiveBetslipConfirmBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}