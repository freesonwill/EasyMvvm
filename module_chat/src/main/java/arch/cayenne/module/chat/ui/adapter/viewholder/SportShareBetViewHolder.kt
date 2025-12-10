package arch.cayenne.module.chat.ui.adapter.viewholder

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.utils.ext.SportDisplayOddsExt.getDisplayOdds
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.utisl.BetSlipUtils
import arch.cayenne.module.chat.databinding.ItemShareBetLayoutBinding
import arch.cayenne.module.order.data.constants.OrderSportPageEnum
import arch.cayenne.module.order.ui.adapter.OrderBettingSelectionAdapter

/**
 * @author: wenxi
 * @date: 10/12/25 15:49
 * @description:
 */
class SportShareBetViewHolder (private val mBinding: ItemShareBetLayoutBinding): BaseViewHolder(mBinding) {

    companion object {
        private const val COLLAPSE_ANIMATION_DURATION = 300L
    }

    private var isExpanded = false
    private var fullSelectionsList: List<BetSlipSelectionData> = emptyList()

    fun init(item: BetSlipOrderBean, type: OrderSportPageEnum) {

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

        mBinding.clCollapse.isVisible = item.selectionsList.size > 1

        if (item.selectionsList.size > 1) {
            setCollapseButtonText(fullSelectionsList.size, false)
            setupCollapseClickListener(selectionAdapter)
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
}