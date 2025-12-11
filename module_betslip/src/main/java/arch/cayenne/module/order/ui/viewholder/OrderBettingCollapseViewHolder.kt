package arch.cayenne.module.order.ui.viewholder

import android.util.TypedValue
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingCollapseBinding
import arch.cayenne.module.order.data.constants.OrderSportPageEnum

class OrderBettingCollapseViewHolder(private val mBinding: ItemOrderSportBettingCollapseBinding): BaseViewHolder(mBinding) {

    fun init(item: BetSlipOrderBean, type: OrderSportPageEnum, onDoubleClick: ((String) -> Unit)? = null) {

        val betAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvBetMoney.text = betAmount
        mBinding.tvCombo.text = if (item.selectionsList.size == 1) {
            mBinding.root.context.getString(arch.cayenne.lib.res.R.string.title_single_bet)
        } else {
            mBinding.root.context.getString(R.string.title_combo_bet_odds, item.comboK, item.comboV)
        }

        val marketName = item.selectionsList.joinToString(",") { it.marketName }
        mBinding.tvMarketName.text = marketName

        val returnAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.returnAmount.getFormalMoney()}"
        mBinding.tvResultMoney.text = returnAmount

        setOrderStatus(item.status, item.comboType == 2)
        setResultStatus(item.resultStatus)
        
        // 設置雙擊監聽
        onDoubleClick?.let { callback ->
            var lastClickTime = 0L
            val doubleClickThreshold = 300L // 300ms 內的兩次點擊視為雙擊
            
            mBinding.root.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < doubleClickThreshold) {
                    // 雙擊
                    callback(item.betId)
                    lastClickTime = 0L // 重置，避免三擊觸發
                } else {
                    // 單擊
                    lastClickTime = currentTime
                }
            }
        }
    }

    private fun setOrderStatus(staus: Int, isFullCombo: Boolean) {
        if (staus == 1) {
            mBinding.tvBetMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
            mBinding.tvCombo.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
        } else if (isFullCombo) {
            mBinding.tvBetMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_00E0E5)
            mBinding.tvCombo.setTextColorRes(arch.cayenne.lib.common.R.color.color_8FBEE9)
        } else {
            mBinding.tvBetMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_00E0E5)
            mBinding.tvCombo.setTextColorRes(arch.cayenne.lib.common.R.color.color_00E0E5)
        }
    }

    private fun setResultStatus(staus: Int) {
        mBinding.clResult.isVisible = staus != 0
        mBinding.tvResultMoney.isVisible = staus == 1 || staus == 2 || staus == 4 || staus == 6 || staus == 7
        mBinding.tvResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (staus == 4 || staus == 5) 11f else 12f)
        when (staus) {
            // 贏
            1, 5 -> {
                mBinding.tvResult.setText(R.string.win)
                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_000000)
                mBinding.tvResult.setFontWeight(500)
                val lp = mBinding.tvResult.layoutParams
                lp.height = 18.dp2px
                lp.width = 18.dp2px
                mBinding.tvResult.layoutParams = lp
                mBinding.tvResult.backgroundTintList = null
                mBinding.tvResult.background = SkinnableResourceManager.getDrawable(itemView.context, R.drawable.bg_order_status_win)

                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_00E301)
            }
            // 和局
            2 -> {

            }
            // 輸
            3, 4 -> {
                mBinding.tvResult.setText(R.string.lose)
                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_2C323E)
                mBinding.tvResult.setFontWeight(500)
                val lp = mBinding.tvResult.layoutParams
                lp.height = 18.dp2px
                lp.width = 18.dp2px
                mBinding.tvResult.layoutParams = lp
                mBinding.tvResult.backgroundTintList = null
                mBinding.tvResult.background = SkinnableResourceManager.getDrawable(itemView.context, R.drawable.bg_order_status_lose)

                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
            }
            // 輸一半
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
            // 贏一半
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
            6 -> {
                mBinding.tvResult.setText(R.string.return_money)
                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_999999)
                mBinding.tvResult.setFontWeight(400)
                val lp = mBinding.tvResult.layoutParams
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                mBinding.tvResult.layoutParams = lp
                mBinding.tvResult.background = null
                mBinding.tvResult.backgroundTintList = null

                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
            }
            // 提前結算
            7 -> {
                mBinding.tvResult.setText(R.string.live_bet_early_settle)
                mBinding.tvResult.setTextColorRes(arch.cayenne.lib.common.R.color.color_2C323E)
                mBinding.tvResult.setFontWeight(500)
                val lp = mBinding.tvResult.layoutParams
                lp.height = 18.dp2px
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                mBinding.tvResult.layoutParams = lp
                mBinding.tvResult.backgroundTintList = null
                mBinding.tvResult.background = SkinnableResourceManager.getDrawable(itemView.context, R.drawable.bg_order_status_early_settle)

                mBinding.tvResultMoney.setTextColorRes(arch.cayenne.lib.common.R.color.color_FFFFFF)
            }
        }
    }
}