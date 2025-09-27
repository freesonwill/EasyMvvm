package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.DateUtils
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.topup.R
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

    companion object {
        const val TYPE_SUC = 1     //提现成功
        const val TYPE_FAD = 2     //提现失败
        const val TYPE_BANK = 1    //银行卡提现
        const val TYPE_USD = 2     //USDT提现
        const val TYPE_EE = 3     //EE钱包提现
    }

    override fun initView(savedInstanceState: Bundle?) {
        val iid = arguments?.getString("iid") ?: ""
        val type = arguments?.getInt("type") ?: 1
        val amount = arguments?.getString("amount") ?: "0.00"
        val account = arguments?.getString("account") ?: "0.00"
        val status = arguments?.getInt("status") ?: 1
        val reason = arguments?.getString("reason") ?: ""
        val time = arguments?.getLong("time") ?: 0L
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.withdrawal_detail.getString(), {
                findNavController().navigateUp()
            })
            tvOrderNumber.text = iid
            tvAmount.text = amount
            tvStatus.text = getStatus(status)
            tvStatus.setTextColor(getStatusTextColor(status))
            tvTime.text = DateUtils.getDisplayStr(time, "yyyy/MM/dd HH:mm:ss")
            if (type == TYPE_BANK) {
                tvOrderId.text = R.string.id_order.getString()
                tvMethod.text = R.string.tx_sk_bank.getString()
                laySk.visibility = View.VISIBLE
                tvMoneySymbol.visibility = View.VISIBLE
                tvMoneySymbol.text = "￥"
                tvSkAccount.text = maskAccountNumber(account, 13)
            } else if (type == TYPE_USD) {
                tvOrderId.text = R.string.tx_address.getString()
                tvMethod.text = R.string.tx_trc.getString()
                laySk.visibility = View.GONE
                tvMoneySymbol.visibility = View.GONE
                ivPayIcon.visibility = View.VISIBLE
                ivPayIcon.background = R.drawable.icon_pay_usdt.getDrawable()
            } else if (type == TYPE_EE) {
                tvOrderId.text = R.string.tx_address.getString()
                tvMethod.text = R.string.tx_ee.getString()
                laySk.visibility = View.GONE
                tvMoneySymbol.visibility = View.GONE
                ivPayIcon.visibility = View.VISIBLE
                ivPayIcon.background = R.drawable.icon_pay_ee.getDrawable()
            }
            if (status == TYPE_FAD) {
                tvFadReason.text = reason
                layFad.visibility = View.VISIBLE
            } else {
                lineCreate.visibility = View.GONE
                layFad.visibility = View.GONE
            }
        }
    }

    override fun initListener() {
        mBinding.ivCopy.clickNoRepeat {
            copyToClipboard(mBinding.tvOrderNumber.text as String?) {
                showToast(R.string.copied_to_clipboard.getString())
            }
        }
    }

    private fun getStatus(status: Int): String {
        return when (status) {
            TYPE_SUC -> R.string.tx_suc.getString()
            TYPE_FAD -> R.string.tx_fad.getString()
            else -> R.string.tx_shen.getString()
        }
    }

    private fun getStatusTextColor(status: Int): Int {
        return when (status) {
            TYPE_SUC -> R.color.pay_success.getColor()
            TYPE_FAD -> R.color.pay_failure.getColor()
            else -> R.color.pay_un_confirm.getColor()
        }
    }

    private fun maskAccountNumber(accountNumber: String?, maskLength: Int): String? {
        if (accountNumber == null || accountNumber.length <= maskLength) {
            return accountNumber
        }
        return "**** **** ****".substring(0, maskLength) + accountNumber.substring(maskLength)
    }

    override suspend fun createObserver() {}
}