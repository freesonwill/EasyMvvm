package com.walisport.module.live.ui.fragment.betslip

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetSlipLayoutBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveBetSlipFragment : BaseFragment<LiveBetSlipViewModel, FragmentLiveBetSlipLayoutBinding>() {
    override val mBinding: FragmentLiveBetSlipLayoutBinding by viewBind()
    override val mViewModel: LiveBetSlipViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}