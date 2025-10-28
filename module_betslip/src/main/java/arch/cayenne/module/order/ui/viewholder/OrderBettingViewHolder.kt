package arch.cayenne.module.order.ui.viewholder

import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.adapter.OrderBettingSelectionAdapter

class OrderBettingViewHolder(private val mBinding: ItemOrderSportBettingBinding): BaseViewHolder(mBinding) {

    fun init(item: BetSlipOrderBean, type: OrderSportPageEnum) {

        val betAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvMoney.text = betAmount
        mBinding.tvCombo.text = if (item.selectionsList.size == 1) {
            mBinding.root.context.getString(R.string.title_single_bet)
        } else {
            mBinding.root.context.getString(R.string.title_combo_bet_odds, item.comboK, item.comboV)
        }

        val selectionAdapter = OrderBettingSelectionAdapter(type)
        mBinding.rvContent.adapter = selectionAdapter
        selectionAdapter.submitList(item.selectionsList)

        val betMoney = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvBetMoney.text = betMoney
        val odds = if (item.selectionsList.size == 1 ) item.odds.getOdds() else item.odds.getDisplayOdds()
        val oddsStr = "@${odds}"
        mBinding.tvBetOdds.text = oddsStr

        val exceptAmount = "${CurrencySymbols.getSymbol(item.currency)}${BetSlipUtils.expectMaxAmount(item.betAmount, item.odds)}"
        mBinding.tvWinMoneyValue.text = exceptAmount
        mBinding.tvWinMoney.setTextRes(if(item.comboType == 0) R.string.live_bet_except_win else R.string.live_bet_except_max_win)

        val settlePrice = BetSlipUtils.earlySettlePrice(
            item.betAmount, item.earlyBetAmount, item.earlySettlePrice.price
        )
        val isCanSettle = settlePrice.toMoney() > 1000 && item.earlySettlePrice.earlySupport
        mBinding.btnEarlySettle.isVisible = isCanSettle
        val earlyAmountStr = "${CurrencySymbols.getSymbol(item.currency)}${settlePrice}"
        mBinding.tvEarlySettleMoney.text = earlyAmountStr
    }
}