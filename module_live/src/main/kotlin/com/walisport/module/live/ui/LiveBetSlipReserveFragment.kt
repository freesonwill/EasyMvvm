package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipReserveBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipReserveViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveBetSlipReserveFragment :BaseFragment<LiveBetSlipReserveViewModel,FragmentLiveBetslipReserveBinding>(){
    override val mBinding: FragmentLiveBetslipReserveBinding by viewBind()
    override val mViewModel: LiveBetSlipReserveViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}