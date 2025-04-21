package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import com.walisport.module.live.databinding.FragmentLiveBetslipExpiredBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipExpiredViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import kotlin.reflect.KClass
//注单失效
class LiveBetSlipExpiredFragment:
    BaseFragment<LiveBetSlipViewModel, FragmentLiveBetslipExpiredBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipExpiredBinding> = FragmentLiveBetslipExpiredBinding::class
    override val vmClass: KClass<LiveBetSlipViewModel> = LiveBetSlipViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}