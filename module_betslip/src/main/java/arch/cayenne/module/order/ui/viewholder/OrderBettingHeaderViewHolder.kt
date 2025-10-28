package arch.cayenne.module.order.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.BetSlipOrderHeaderBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingHeaderBinding

class OrderBettingHeaderViewHolder(private val mBinding: ItemOrderSportBettingHeaderBinding): BaseViewHolder(mBinding) {

    fun init(item: BetSlipOrderHeaderBean) {
        mBinding.tvDate.text = item.dateTime
        val betAmount = itemView.context.getString(R.string.title_order_sport_header_betting_amount).format("${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}")
        mBinding.tvBetting.text = betAmount

        val validBetAmount = itemView.context.getString(R.string.title_order_sport_header_valid_betting_amount).format("${CurrencySymbols.getSymbol(item.currency)}${item.validBetAmount.getFormalMoney()}")
        mBinding.tvValidBetting.text = validBetAmount
    }
}