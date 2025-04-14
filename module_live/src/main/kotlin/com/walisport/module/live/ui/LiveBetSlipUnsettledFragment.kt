package com.walisport.module.live.ui

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetSlipLayoutBinding
import com.walisport.module.live.databinding.FragmentLiveBetslipUnsettledBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipUnsettledViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass
//注单未结算
class LiveBetSlipUnsettledFragment :
    BaseFragment<LiveBetSlipUnsettledViewModel, FragmentLiveBetslipUnsettledBinding>() {
    override val vbClass: KClass<FragmentLiveBetslipUnsettledBinding> = FragmentLiveBetslipUnsettledBinding::class
    override val vmClass: KClass<LiveBetSlipUnsettledViewModel> = LiveBetSlipUnsettledViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    private fun initRecycler() {


    }
}