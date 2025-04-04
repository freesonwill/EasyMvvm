package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipSettledLayoutBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipSettledViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveBetSlipSettledFragment:BaseFragment<LiveBetSlipSettledViewModel,FragmentLiveBetslipSettledLayoutBinding>() {
    override val mBinding: FragmentLiveBetslipSettledLayoutBinding by viewBind()
    override val mViewModel: LiveBetSlipSettledViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}