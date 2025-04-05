package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipSettledLayoutBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipSettledViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class LiveBetSlipSettledFragment:BaseFragment<LiveBetSlipSettledViewModel,FragmentLiveBetslipSettledLayoutBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipSettledLayoutBinding> = FragmentLiveBetslipSettledLayoutBinding::class
    override val vmClass: KClass<LiveBetSlipSettledViewModel> = LiveBetSlipSettledViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}