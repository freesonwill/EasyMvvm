package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipInvalidBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipInvalidViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

class LiveBetSlipInvalidFragment:BaseFragment<LiveBetSlipInvalidViewModel,FragmentLiveBetslipInvalidBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipInvalidBinding> = FragmentLiveBetslipInvalidBinding::class
    override val vmClass: KClass<LiveBetSlipInvalidViewModel> = LiveBetSlipInvalidViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}