package arch.cayenne.module.order.ui.viewholder

import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingCollapseBinding
import arch.cayenne.module.order.data.constants.OrderSportPageEnum

class OrderBettingCollapseViewHolder(private val mBinding: ItemOrderSportBettingCollapseBinding): BaseViewHolder(mBinding) {

    fun init(item: BetSlipOrderBean, type: OrderSportPageEnum) {

        val betAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvBetMoney.text = betAmount
        mBinding.tvCombo.text = if (item.selectionsList.size == 1) {
            mBinding.root.context.getString(arch.cayenne.lib.res.R.string.title_single_bet)
        } else {
            mBinding.root.context.getString(R.string.title_combo_bet_odds, item.comboK, item.comboV)
        }

        val marketName = item.selectionsList.joinToString(",") { it.marketName }
        mBinding.tvMarketName.text = marketName

        mBinding.clResult.isVisible = item.resultStatus == 0
    }
}