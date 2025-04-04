package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipConfirmBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipConfirmViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveBetSlipConfirmFragment:
    BaseFragment<LiveBetSlipConfirmViewModel, FragmentLiveBetslipConfirmBinding>() {
    override val mBinding: FragmentLiveBetslipConfirmBinding by viewBind()
    override val mViewModel: LiveBetSlipConfirmViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}