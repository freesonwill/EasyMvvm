package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.topup.databinding.DialogWithdrawTipBinding
import kotlin.reflect.KClass

/**
 * 提现订单已提交弹窗
 */

class WithdrawTipFragment :
    BaseBottomSheetFragment<EmptyViewModel, DialogWithdrawTipBinding>() {

    override val vbClass: KClass<DialogWithdrawTipBinding> = DialogWithdrawTipBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.tvCheckDetail.clickNoRepeat {
            dismiss()
        }
        mBinding.tvConfirm.clickNoRepeat {
            dismiss()
        }
    }

    companion object {
        private const val ARG_IID = "arg_iid"

        fun newInstance(
            iid: String
        ) = WithdrawTipFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_IID, iid)
            }
        }
    }
}