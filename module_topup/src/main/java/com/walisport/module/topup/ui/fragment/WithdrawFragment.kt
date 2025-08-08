package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentWithdrawBinding
import com.walisport.module.topup.ui.viewmodel.TopUpMainViewModel
import kotlin.reflect.KClass

/**
 * 提现页面
 */

class WithdrawFragment : BaseFragment<TopUpMainViewModel, FragmentWithdrawBinding>() {

    override val vbClass: KClass<FragmentWithdrawBinding> = FragmentWithdrawBinding::class
    override val vmClass: KClass<TopUpMainViewModel> = TopUpMainViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.withdrawal.getString(), {
                findNavController().navigateUp()
            })
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }

}