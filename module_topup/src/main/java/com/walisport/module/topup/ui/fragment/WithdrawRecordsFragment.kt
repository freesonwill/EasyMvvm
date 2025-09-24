package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentWithdrawRecordsBinding
import com.walisport.module.topup.ui.viewmodel.WithdrawRecordsViewModel
import kotlin.reflect.KClass

/**
 * 充值记录记录列表页
 */

class WithdrawRecordsFragment :
    BaseFragment<WithdrawRecordsViewModel, FragmentWithdrawRecordsBinding>() {

    override val vbClass: KClass<FragmentWithdrawRecordsBinding> =
        FragmentWithdrawRecordsBinding::class
    override val vmClass: KClass<WithdrawRecordsViewModel> = WithdrawRecordsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.withdrawal_record.getString(), {
                findNavController().navigateUp()
            })
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}