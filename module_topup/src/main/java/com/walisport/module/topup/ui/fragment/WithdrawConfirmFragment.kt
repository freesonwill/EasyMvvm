package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import androidx.fragment.app.setFragmentResult
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.topup.databinding.DialogWithdrawConfirmBinding
import kotlin.reflect.KClass

/**
 * 提现确认弹窗
 */

class WithdrawConfirmFragment :
    BaseBottomSheetFragment<EmptyViewModel, DialogWithdrawConfirmBinding>() {

    override val vbClass: KClass<DialogWithdrawConfirmBinding> = DialogWithdrawConfirmBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            val address = it.getString(ARG_ADDRESS)
            mBinding.tvAddress.text = address
            val coin = it.getString(ARG_COIN)
            mBinding.tvCoin.text = coin
        }
    }

    override fun initListener() {
        mBinding.ivIconClose.clickNoRepeat {
            dismiss()
        }
        mBinding.tvCancel.clickNoRepeat {
            dismiss()
        }
        mBinding.tvConfirm.clickNoRepeat {
            val result = Bundle().apply {
                putString("iid", "")
            }
            setFragmentResult(WithdrawCryptoFragment.TIP, result)
            this@WithdrawConfirmFragment.dismiss()
            this@WithdrawConfirmFragment.dialog?.dismiss()
        }
    }

    companion object {
        private const val ARG_ADDRESS = "arg_address"
        private const val ARG_COIN = "arg_coin"

        fun newInstance(
            address: String,
            coin: String
        ) = WithdrawConfirmFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ADDRESS, address)
                putString(ARG_COIN, coin)
            }
        }
    }
}