package arch.cayenne.module.order.ui.viewholder

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils

class OrderBettingViewHolder(mBinding: ItemOrderSportBettingBinding): BaseOrderViewHolder<BetSlipOrderBean>(mBinding) {

    override fun init(item: BetSlipOrderBean) {
        val betAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvMoney.text = betAmount
        mBinding.tvCombo.text = if (item.selectionsList.size == 1) {
            mBinding.root.context.getString(arch.cayenne.lib.res.R.string.title_single_bet)
        } else {
            mBinding.root.context.getString(R.string.title_combo_bet_odds, item.comboK, item.comboV)
        }

//        mBinding.rvContent.addItemDecoration(OrderItemSelectionDecoration(18.dp2px))


        // 確保 RecyclerView 高度為 wrap_content
        val layoutParams = mBinding.rvContent.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mBinding.rvContent.layoutParams = layoutParams

        setBetMoney(item.betAmount, item.currency)
        setOdds(item.selectionsList.size == 1, item.odds, item.resultStatus)

        setResultStatus(item.resultStatus, item.comboType == 0)
        mBinding.tvResultMoney.text = if (item.resultStatus == 0) {
            "${CurrencySymbols.getSymbol(item.currency)}${BetSlipUtils.expectMaxAmount(item.betAmount, item.odds)}"
        } else {
            "${CurrencySymbols.getSymbol(item.currency)}${item.returnAmount.getFormalMoney()}"
        }

        val settlePrice = BetSlipUtils.earlySettlePrice(
            item.betAmount, item.earlyBetAmount, item.earlySettlePrice.price
        )
        val isCanSettle = settlePrice.toMoney() > 1000 && item.earlySettlePrice.earlySupport
        mBinding.clBottomButton.isVisible = isCanSettle
        mBinding.btnEarlySettle.isVisible = isCanSettle
        val earlyAmountStr = "${CurrencySymbols.getSymbol(item.currency)}${settlePrice}"
        mBinding.tvEarlySettleMoney.text = earlyAmountStr

        mBinding.clCollapse.isVisible = item.selectionsList.size > 1
    }

    private fun setBetMoney(betMoney: Long, symbol: String) {
        val betMoney = "${CurrencySymbols.getSymbol(symbol)}${betMoney.getFormalMoney()}"
        mBinding.tvBetMoney.text = betMoney
    }

    private fun setOdds(isSingleBet: Boolean, odds: Int, status: Int) {
        val odds = if (isSingleBet) odds.getOdds() else odds.getDisplayOdds()
        val oddsStr = "@${odds}"
        mBinding.tvBetOdds.text = oddsStr
        mBinding.clBetOdds.visibility = if (status == 0) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun setResultStatus(status: Int, isSingleBet: Boolean) {
        when (status) {
            0 -> {
                mBinding.tvResult.setText(if(isSingleBet) R.string.live_bet_except_win else R.string.live_bet_except_max_win)
                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_999999)
                mBinding.tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                mBinding.tvResult.setFontWeight(400)
                val lp = mBinding.tvResult.layoutParams
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                mBinding.tvResult.layoutParams = lp
                mBinding.tvResult.backgroundTintList = null
                mBinding.tvResult.background = null

                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
            }
            // 贏
            1, 5 -> {
                mBinding.tvResult.setText(R.string.win)
                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_000000)
                mBinding.tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                mBinding.tvResult.setFontWeight(500)
                val lp = mBinding.tvResult.layoutParams
                lp.height = 21.dp2px
                lp.width = 21.dp2px
                mBinding.tvResult.layoutParams = lp
                mBinding.tvResult.backgroundTintList = null
                mBinding.tvResult.background = SkinnableResourceManager.getDrawable(itemView.context, R.drawable.bg_order_status_win)

                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_00E301)
            }
            // 和局
            2 -> {

            }
            // 輸
            3, 4, 6 -> {
                mBinding.tvResult.setText(R.string.return_money)
                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_999999)
                mBinding.tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                mBinding.tvResult.setFontWeight(400)
                val lp = mBinding.tvResult.layoutParams
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                mBinding.tvResult.layoutParams = lp
                mBinding.tvResult.backgroundTintList = null
                mBinding.tvResult.background = null

                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
            }
//            // 輸一半
//            4 -> {
//                mBinding.tvResult.setText(R.string.order_lose_half)
//                mBinding.tvResult.setPadding(7.dp2px, 3.dp2px, 6.dp2px, 2.dp2px)
//                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
//
//                mBinding.tvResult.background = SkinnableResourceManager.getDrawable(itemView.context, R.drawable.shape_4dp)
//                mBinding.tvResult.backgroundTintList = SkinnableResourceManager.getColorStateList(itemView.context, arch.cayenne.lib.common.R.color.color_A50111)
//
//                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
//            }
//            // 贏一半
//            5 -> {
//                mBinding.tvResult.setText(R.string.order_win_half)
//                mBinding.tvResult.setPadding(7.dp2px, 3.dp2px, 6.dp2px, 2.dp2px)
//                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
//
//                mBinding.tvResult.background = SkinnableResourceManager.getDrawable(itemView.context, R.drawable.shape_4dp)
//                mBinding.tvResult.backgroundTintList = SkinnableResourceManager.getColorStateList(itemView.context, arch.cayenne.lib.common.R.color.color_00B001)
//
//                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
//            }
            // 退款
//            6 -> {
//                mBinding.tvResult.setText(R.string.return_money)
//                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_999999)
//                mBinding.tvResult.background = null
//                mBinding.tvResult.backgroundTintList = null
//
//                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
//            }
            // 提前結算
            7 -> {
                mBinding.tvResult.setText(R.string.live_bet_early_settle)
                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_2C323E)
                mBinding.tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                mBinding.tvResult.setFontWeight(500)
                val lp = mBinding.tvResult.layoutParams
                lp.height = 21.dp2px
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                mBinding.tvResult.layoutParams = lp
                mBinding.tvResult.backgroundTintList = null
                mBinding.tvResult.background = SkinnableResourceManager.getDrawable(itemView.context, R.drawable.bg_order_status_early_settle)

                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
            }
        }
    }

    fun getEarlySettleButton(): View {
        return mBinding.ivShare
    }
}