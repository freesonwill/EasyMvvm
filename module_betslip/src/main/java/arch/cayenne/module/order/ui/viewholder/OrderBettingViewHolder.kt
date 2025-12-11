package arch.cayenne.module.order.ui.viewholder

import android.animation.ValueAnimator
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.adapter.OrderBettingSelectionAdapter

class OrderBettingViewHolder(private val mBinding: ItemOrderSportBettingBinding): BaseViewHolder(mBinding) {

    companion object {
        private const val COLLAPSE_ANIMATION_DURATION = 300L
    }

    private var isExpanded = false
    private var fullSelectionsList: List<BetSlipSelectionData> = emptyList()

    fun init(item: BetSlipOrderBean, type: OrderSportPageEnum, onDoubleClick: ((String) -> Unit)? = null) {

        val betAmount = "${CurrencySymbols.getSymbol(item.currency)}${item.betAmount.getFormalMoney()}"
        mBinding.tvMoney.text = betAmount
        mBinding.tvCombo.text = if (item.selectionsList.size == 1) {
            mBinding.root.context.getString(arch.cayenne.lib.res.R.string.title_single_bet)
        } else {
            mBinding.root.context.getString(R.string.title_combo_bet_odds, item.comboK, item.comboV)
        }

        fullSelectionsList = item.selectionsList
        
        val selectionAdapter = OrderBettingSelectionAdapter(type)
        mBinding.rvContent.adapter = selectionAdapter
//        mBinding.rvContent.addItemDecoration(OrderItemSelectionDecoration(18.dp2px))

        val initialList = if (item.selectionsList.size > 1) {
            listOf(item.selectionsList.first())
        } else {
            item.selectionsList
        }
        selectionAdapter.submitList(initialList)
        isExpanded = false
        
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
        mBinding.btnEarlySettle.isVisible = isCanSettle
        val earlyAmountStr = "${CurrencySymbols.getSymbol(item.currency)}${settlePrice}"
        mBinding.tvEarlySettleMoney.text = earlyAmountStr

        mBinding.clCollapse.isVisible = item.selectionsList.size > 1

        if (item.selectionsList.size > 1) {
            setCollapseButtonText(fullSelectionsList.size, false)
            setupCollapseClickListener(selectionAdapter)
        }
        
        // 設置雙擊監聽
        onDoubleClick?.let { callback ->
            var lastClickTime = 0L
            val doubleClickThreshold = 300L // 300ms 內的兩次點擊視為雙擊
            
            // 為整個 item 設置雙擊監聽
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
            
            // 為 RecyclerView 設置觸摸監聽來檢測雙擊
            var rvLastClickTime = 0L
            mBinding.rvContent.setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - rvLastClickTime < doubleClickThreshold) {
                        // 雙擊
                        callback(item.betId)
                        rvLastClickTime = 0L // 重置，避免三擊觸發
                        return@setOnTouchListener true // 消費事件
                    } else {
                        // 單擊
                        rvLastClickTime = currentTime
                    }
                }
                false // 不消費事件，讓 RecyclerView 正常工作
            }
        }
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

    private fun setResultStatus(staus: Int, isSingleBet: Boolean) {
        when (staus) {
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

    private fun setupCollapseClickListener(adapter: OrderBettingSelectionAdapter) {
        mBinding.clCollapse.setOnClickListener {
            toggleExpansion(adapter)
        }
    }

    private fun toggleExpansion(adapter: OrderBettingSelectionAdapter) {
        isExpanded = !isExpanded

        setCollapseButtonText(fullSelectionsList.size, isExpanded)
        
        if (isExpanded) {
            // 展開：先測量完整高度，然後動畫展開
            expandRecyclerView(adapter)
        } else {
            // 收起：動畫收起到第一個項目的高度
            collapseRecyclerView(adapter)
        }
        
        animateCollapseIcon()
    }

    private fun setCollapseButtonText(size: Int, isExpanded: Boolean) {
        if (isExpanded) {
            mBinding.tvCollapse.text = mBinding.root.context.getString(R.string.title_order_sport_collapse)
        } else {
            mBinding.tvCollapse.text = mBinding.root.context.getString(R.string.title_order_sport_expand, size)
        }
    }

    private fun expandRecyclerView(adapter: OrderBettingSelectionAdapter) {
        // 先測量收起狀態的高度
        val collapsedHeight = mBinding.rvContent.height
        
        // 提交完整列表
        adapter.submitList(fullSelectionsList) {
            // 測量展開後的高度
            mBinding.rvContent.measure(
                View.MeasureSpec.makeMeasureSpec(mBinding.rvContent.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val expandedHeight = mBinding.rvContent.measuredHeight
            
            // 創建高度動畫
            val animator = ValueAnimator.ofInt(collapsedHeight, expandedHeight)
            animator.duration = 300
            animator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                val layoutParams = mBinding.rvContent.layoutParams
                layoutParams.height = animatedValue
                mBinding.rvContent.layoutParams = layoutParams
            }
            animator.start()
        }
    }

    private fun collapseRecyclerView(adapter: OrderBettingSelectionAdapter) {
        // 當前展開的高度
        val expandedHeight = mBinding.rvContent.height
        
        // 提交收起列表
        adapter.submitList(listOf(fullSelectionsList.first())) {
            // 測量收起後的高度
            mBinding.rvContent.measure(
                View.MeasureSpec.makeMeasureSpec(mBinding.rvContent.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val collapsedHeight = mBinding.rvContent.measuredHeight
            
            // 創建高度動畫
            val animator = ValueAnimator.ofInt(expandedHeight, collapsedHeight)
            animator.duration = COLLAPSE_ANIMATION_DURATION
            animator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
                val layoutParams = mBinding.rvContent.layoutParams
                layoutParams.height = animatedValue
                mBinding.rvContent.layoutParams = layoutParams
            }
            animator.start()
        }
    }

    private fun animateCollapseIcon() {
        val rotation = if (isExpanded) 180f else 0f
        mBinding.ivCollapse.animate()
            .rotation(rotation)
            .setDuration(COLLAPSE_ANIMATION_DURATION)
            .start()
    }

    fun getEarlySettleButton(): View {
        return mBinding.ivShare
    }
}