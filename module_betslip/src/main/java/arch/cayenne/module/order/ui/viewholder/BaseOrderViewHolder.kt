package arch.cayenne.module.order.ui.viewholder

import android.animation.ValueAnimator
import android.view.View
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipSelectionData
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingBinding
import arch.cayenne.module.order.ui.adapter.OrderBettingSelectionAdapter

abstract class BaseOrderViewHolder<T: BetSlipData>(protected val mBinding: ItemOrderSportBettingBinding): BaseViewHolder(mBinding) {


    companion object {
        const val COLLAPSE_ANIMATION_DURATION = 300L
    }

    private var lastClickTime = 0L
    private var isExpanded = false
    private var fullSelectionsList: List<BetSlipSelectionData> = emptyList()

    abstract fun init(item: T)

    fun initSelection(selections: List<BetSlipSelectionData>) {
        fullSelectionsList = selections

        val selectionAdapter = OrderBettingSelectionAdapter()
        mBinding.rvContent.adapter = selectionAdapter

        val initialList = if (selections.size > 1) {
            listOf(selections.first())
        } else {
            selections
        }
        selectionAdapter.submitList(initialList)
        isExpanded = false

        if (selections.size > 1) {
            setCollapseButtonText(fullSelectionsList.size, false)
            setupCollapseClickListener(selectionAdapter)
        }
    }

    fun setDoubleClick(onDoubleClick: () -> Unit) {
        mBinding.root.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < COLLAPSE_ANIMATION_DURATION) {
                onDoubleClick.invoke()
                lastClickTime = 0L
            } else {
                lastClickTime = currentTime
            }
        }

        mBinding.rvContent.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < COLLAPSE_ANIMATION_DURATION) {
                    // 雙擊
                    onDoubleClick.invoke()
                    lastClickTime = 0L // 重置，避免三擊觸發
                    return@setOnTouchListener true // 消費事件
                } else {
                    // 單擊
                    lastClickTime = currentTime
                }
            }
            false // 不消費事件，讓 RecyclerView 正常工作
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