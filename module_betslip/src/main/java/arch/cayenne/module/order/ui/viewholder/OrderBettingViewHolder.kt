package arch.cayenne.module.order.ui.viewholder

import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.adapter.OrderBettingSelectionAdapter

class OrderBettingViewHolder(private val mBinding: ItemOrderSportBettingBinding): BaseViewHolder(mBinding) {

    fun init(item: BetSlipOrderBean, type: OrderSportPageEnum) {
        val selectionAdapter = OrderBettingSelectionAdapter(type)
        mBinding.rvContent.adapter = selectionAdapter

        mBinding.tvBetOdds.text = item.odds.getOdds()

        val settlePrice = BetSlipUtils.earlySettlePrice(
            item.betAmount, item.earlyBetAmount, item.earlySettlePrice.price
        )
        val isCanSettle = settlePrice.toMoney() > 1000 && item.earlySettlePrice.earlySupport
        mBinding.btnEarlySettle.isVisible = isCanSettle
        val earlyAmountStr = "${CurrencySymbols.getSymbol(item.currency)}${settlePrice}"
        mBinding.tvEarlySettleMoney.text = earlyAmountStr
    }
}