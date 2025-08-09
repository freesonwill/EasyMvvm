package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import com.walisport.module.topup.databinding.FragmentWithdrawDetailBinding
import com.walisport.module.topup.ui.viewmodel.WithdrawDetailViewModel
import kotlin.reflect.KClass

/**
 * 提现记录详情页
 */
class WithdrawDetailFragment :
    BaseFragment<WithdrawDetailViewModel, FragmentWithdrawDetailBinding>() {

    override val vbClass: KClass<FragmentWithdrawDetailBinding> =
        FragmentWithdrawDetailBinding::class
    override val vmClass: KClass<WithdrawDetailViewModel> = WithdrawDetailViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}