package com.walisport.module.topup.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.DateUtils
import arch.cayenne.lib.common.utils.copyToClipboard
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.touchBackPressed
import arch.cayenne.lib.common.utils.helper.showToast
import com.walisport.module.topup.R
import com.walisport.module.topup.databinding.FragmentTopupDetailBinding
import com.walisport.module.topup.ui.viewmodel.TopUpDetailViewModel
import kotlin.reflect.KClass

/**
 * 充值记录详情页
 */

class TopUpDetailFragment : BaseFragment<TopUpDetailViewModel, FragmentTopupDetailBinding>() {

    override val vbClass: KClass<FragmentTopupDetailBinding> = FragmentTopupDetailBinding::class
    override val vmClass: KClass<TopUpDetailViewModel> = TopUpDetailViewModel::class

    companion object {
        const val TYPE_SUC = 1     //充值成功
        const val TYPE_FAD = 2     //充值失败

        const val TYPE_BANK = 1    //银行卡充值
        const val TYPE_ALI = 2     //支付宝充值
        const val TYPE_WECHAT = 3  //微信充值
        const val TYPE_YUN = 4     //云闪付充值
        const val TYPE_EE = 5      //EE钱包充值
        const val TYPE_RMB = 6     //数字人民币充值
    }

    override fun initView(savedInstanceState: Bundle?) {
        val iid = arguments?.getString("iid") ?: ""
        val type = arguments?.getInt("type") ?: 1
        val amount = arguments?.getString("amount") ?: "0.00"
        val status = arguments?.getInt("status") ?: 1
        val time = arguments?.getLong("time") ?: 0L
        with(mBinding) {
            titleBar.loadGeneralTitleBar(R.string.recharge_detail.getString(), {
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
                tvMoneySymbol.visibility = View.VISIBLE
                tvMoneySymbol.text = "￥"
            } else if (type == TYPE_ALI) {
                tvOrderId.text = R.string.id_order.getString()
                tvMethod.text = R.string.tx_ali.getString()
                tvMoneySymbol.visibility = View.GONE
                ivPayIcon.visibility = View.VISIBLE
                ivPayIcon.background = R.drawable.icon_pay_ali.getDrawable()
            } else if (type == TYPE_WECHAT) {
                tvOrderId.text = R.string.id_order.getString()
                tvMethod.text = R.string.tx_ali.getString()
                tvMoneySymbol.visibility = View.GONE
                ivPayIcon.visibility = View.VISIBLE
                ivPayIcon.background = R.drawable.icon_pay_wechat.getDrawable()
            } else if (type == TYPE_YUN) {
                tvOrderId.text = R.string.cz_address.getString()
                tvMethod.text = R.string.tx_yun.getString()
                tvMoneySymbol.visibility = View.GONE
                ivPayIcon.visibility = View.VISIBLE
                ivPayIcon.background = R.drawable.icon_pay_yun.getDrawable()
            } else if (type == TYPE_EE) {
                tvOrderId.text = R.string.cz_address.getString()
                tvMethod.text = R.string.tx_ee.getString()
                tvMoneySymbol.visibility = View.GONE
                ivPayIcon.visibility = View.VISIBLE
                ivPayIcon.background = R.drawable.icon_pay_ee.getDrawable()
            } else if (type == TYPE_RMB) {
                tvOrderId.text = R.string.cz_address.getString()
                tvMethod.text = R.string.tx_rmb.getString()
                tvMoneySymbol.visibility = View.GONE
                ivPayIcon.visibility = View.VISIBLE
                ivPayIcon.background = R.drawable.icon_pay_rmb.getDrawable()
            }
        }
        mBinding.root.touchBackPressed()
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
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
            TYPE_SUC -> R.string.cz_suc.getString()
            TYPE_FAD -> R.string.cz_fad.getString()
            else -> R.string.cz_shen.getString()
        }
    }

    private fun getStatusTextColor(status: Int): Int {
        return when (status) {
            TYPE_SUC -> R.color.pay_success.getColor()
            TYPE_FAD -> R.color.pay_failure.getColor()
            else -> R.color.pay_un_confirm.getColor()
        }
    }

    override suspend fun createObserver() {}
}