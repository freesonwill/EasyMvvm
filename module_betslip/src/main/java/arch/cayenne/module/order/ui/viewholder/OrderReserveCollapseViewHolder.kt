package arch.cayenne.module.order.ui.viewholder

import androidx.core.view.isVisible
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingCollapseBinding

class OrderReserveCollapseViewHolder(mBinding: ItemOrderSportBettingCollapseBinding): BaseOrderCollapseViewHolder<BetSlipReserveBean>(mBinding) {

    override fun init(item: BetSlipReserveBean) {
        val betAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvBetMoney.text = betAmount
        mBinding.tvCombo.text = mBinding.root.context.getString(arch.cayenne.lib.res.R.string.title_single_bet)

        val marketName = item.selection.marketName
        mBinding.tvMarketName.text = marketName

        // 預約訂單不顯示返還金額
        mBinding.tvResultMoney.isVisible = false
    }
}