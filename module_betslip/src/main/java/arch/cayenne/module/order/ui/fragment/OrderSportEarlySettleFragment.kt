package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.LayoutOrderSportEarlySettleBinding
import arch.cayenne.module.order.ui.viewmodel.OrderSportEarlySettleViewModel
import kotlin.reflect.KClass

class OrderSportEarlySettleFragment private constructor() :
    BaseBottomSheetFragment<OrderSportEarlySettleViewModel, LayoutOrderSportEarlySettleBinding>() {

    companion object {

        private const val BET_AMOUNT_MONEY = "bet_amount_money"
        private const val BET_AMOUNT_CURRENCY = "bet_amount_currency"

        fun newInstance(
            money: String,
            currency: String
        ): OrderSportEarlySettleFragment {
            val fragment = OrderSportEarlySettleFragment()
            fragment.arguments = Bundle().apply {
                putString(BET_AMOUNT_MONEY, money)
                putString(BET_AMOUNT_CURRENCY, currency)
            }
            return fragment
        }
    }

    override val vbClass: KClass<LayoutOrderSportEarlySettleBinding> =
        LayoutOrderSportEarlySettleBinding::class
    override val vmClass: KClass<OrderSportEarlySettleViewModel> =
        OrderSportEarlySettleViewModel::class

    private var confirmListener: (() -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {
        val betAmount = requireArguments().getString(BET_AMOUNT_MONEY) ?: "0"
        val currencyStr = requireArguments().getString(BET_AMOUNT_CURRENCY) ?: "CYN"
        val currencySymbol = CurrencySymbols.getSymbol(currencyStr)
        val confirmRes = getString(R.string.title_order_sport_early_settle_confirm)
        val confirmText = "$confirmRes $currencySymbol$betAmount"
        mBinding.tvConfirm.text = confirmText
    }

    override fun initListener() {
        mBinding.tvConfirm.setOnClickListener {
            confirmListener?.invoke()
            dismiss()
        }
        mBinding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnConfirmListener(listener: () -> Unit) {
        this.confirmListener = listener
    }
}