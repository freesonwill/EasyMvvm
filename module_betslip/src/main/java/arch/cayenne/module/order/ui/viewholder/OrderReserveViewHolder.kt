package arch.cayenne.module.order.ui.viewholder

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.adapter.OrderBettingSelectionAdapter

class OrderReserveViewHolder(mBinding: ItemOrderSportBettingBinding): BaseOrderViewHolder<BetSlipReserveBean>(mBinding) {

    override fun init(item: BetSlipReserveBean) {
        val betAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvMoney.text = betAmount
        mBinding.tvCombo.text = mBinding.root.context.getString(arch.cayenne.lib.res.R.string.title_single_bet)

        // 預約訂單通常是單關，所以直接顯示單個選項
        val selectionAdapter = OrderBettingSelectionAdapter()
        mBinding.rvContent.adapter = selectionAdapter

        // 將 ReserveOrderSelectionBean 轉換為顯示用的資料
        val selectionList = listOf(item.selection)
        selectionAdapter.submitList(selectionList)

        // 確保 RecyclerView 高度為 wrap_content
        val layoutParams = mBinding.rvContent.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mBinding.rvContent.layoutParams = layoutParams

        setBetMoney(item.betAmount, item.currency)
        setReserveOdds(item.selection.odds)

        // 預約訂單不顯示提前結算相關內容
        mBinding.clBottomButton.isVisible = true
        mBinding.btnEarlySettle.isVisible = false
        mBinding.groupReserve.isVisible = true

        initSelection(listOf(item.selection))
    }

    private fun setBetMoney(betMoney: Long, symbol: String) {
        val betMoney = "${CurrencySymbols.getSymbol(symbol)}${betMoney.getFormalMoney()}"
        mBinding.tvBetMoney.text = betMoney
    }

    private fun setReserveOdds(odds: Int) {
        val oddsStr = "@${odds.getDisplayOdds()}"
        mBinding.tvBetOdds.text = oddsStr
        mBinding.clBetOdds.visibility = View.VISIBLE
    }
}