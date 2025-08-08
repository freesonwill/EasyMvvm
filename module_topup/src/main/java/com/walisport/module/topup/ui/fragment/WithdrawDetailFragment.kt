package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.topup.databinding.FragmentWithdrawDetailBinding
import com.walisport.module.topup.ui.viewmodel.TopUpMainViewModel
import kotlin.reflect.KClass

class WithdrawDetailFragment : BaseFragment<TopUpMainViewModel, FragmentWithdrawDetailBinding>() {

    override val vbClass: KClass<FragmentWithdrawDetailBinding> = FragmentWithdrawDetailBinding::class
    override val vmClass: KClass<TopUpMainViewModel> = TopUpMainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}