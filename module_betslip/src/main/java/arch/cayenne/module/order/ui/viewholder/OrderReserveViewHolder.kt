package arch.cayenne.module.order.ui.viewholder

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding

class OrderReserveViewHolder(mBinding: ItemOrderSportBettingBinding) :
    BaseOrderViewHolder<BetSlipReserveBean>(mBinding) {

    override fun init(item: BetSlipReserveBean) {
        val betAmount =
            "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvMoney.text = betAmount
        mBinding.tvCombo.text =
            mBinding.root.context.getString(arch.cayenne.lib.res.R.string.title_single_bet)

        // 確保 RecyclerView 高度為 wrap_content
        val layoutParams = mBinding.rvContent.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mBinding.rvContent.layoutParams = layoutParams

        setBetMoney(item.betAmount, item.currency)

        val resultMoney =
            "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvResultMoney.text = resultMoney
        setResultStatus(true)

        // 預約訂單不顯示提前結算相關內容
        mBinding.clBetOdds.visibility = View.INVISIBLE
        mBinding.clBottomButton.isVisible = true
        mBinding.btnEarlySettle.isVisible = false
        mBinding.groupReserve.isVisible = true

    }

    private fun setBetMoney(betMoney: Long, symbol: String) {
        val betMoney = "${CurrencySymbols.getSymbol(symbol)}${betMoney.getFormalMoney()}"
        mBinding.tvBetMoney.text = betMoney
    }

    private fun setResultStatus(isSingleBet: Boolean) {
        mBinding.tvResult.setText(if (isSingleBet) R.string.live_bet_except_win else R.string.live_bet_except_max_win)
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

    fun setCancelButtonClickListener(listener: () -> Unit) {
        mBinding.btnCancelReserve.setOnClickListener {
            listener.invoke()
        }
    }

    fun setModifyButtonClickListener(listener: (locationX: Int, locationY: Int, viewHeight: Int) -> Unit) {
        mBinding.btnModifyReserve.setOnClickListener { view ->
            val h = ViewUtils.getStatusBarHeight(view.context)
            val location = IntArray(2)
            view.getLocationInWindow(location)
            listener.invoke(
                location.first() + view.width / 2,
                location.last() - h,
                view.height
            )
        }
    }
}