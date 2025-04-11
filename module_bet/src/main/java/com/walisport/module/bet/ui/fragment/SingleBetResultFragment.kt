package com.walisport.module.bet.ui.fragment

import android.os.Bundle
import com.walisport.lib.base.ui.BaseFragment
import com.walisport.lib.base.ui.sendResult
import com.walisport.module.bet.R
import com.walisport.module.bet.databinding.FragmentSingleBetResultBinding
import com.walisport.module.bet.viewmodel.SingleBetResultViewModel
import kotlin.reflect.KClass

class SingleBetResultFragment : BaseFragment<SingleBetResultViewModel, FragmentSingleBetResultBinding>(), BetSheetListener {
    override val vbClass: KClass<FragmentSingleBetResultBinding> = FragmentSingleBetResultBinding::class
    override val vmClass: KClass<SingleBetResultViewModel> = SingleBetResultViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        mBinding.tvTest.setOnClickListener {
            dismiss()
        }
    }

    override fun createObserver() {
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.singleBetResultFragment)
    }
}