package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.topup.databinding.FragmentWithdrawDetailBinding
import com.walisport.module.topup.ui.viewmodel.WithdrawViewModel
import kotlin.reflect.KClass

class WithdrawDetailFragment : BaseFragment<WithdrawViewModel, FragmentWithdrawDetailBinding>() {

    override val vbClass: KClass<FragmentWithdrawDetailBinding> = FragmentWithdrawDetailBinding::class
    override val vmClass: KClass<WithdrawViewModel> = WithdrawViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}