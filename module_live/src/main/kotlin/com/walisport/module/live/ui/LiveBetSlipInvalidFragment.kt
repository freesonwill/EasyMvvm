package com.walisport.module.live.ui

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.viewBind
import com.walisport.module.live.databinding.FragmentLiveBetslipInvalidBinding
import com.walisport.module.live.ui.viewmodel.LiveBetSlipInvalidViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LiveBetSlipInvalidFragment:BaseFragment<LiveBetSlipInvalidViewModel,FragmentLiveBetslipInvalidBinding>() {
    override val mBinding: FragmentLiveBetslipInvalidBinding by viewBind()
    override val mViewModel: LiveBetSlipInvalidViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override fun createObserver() {
    }
}