package arch.cayenne.module.order.ui.viewholder

import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.module.betslip.databinding.ItemOrderSportBettingCollapseBinding

abstract class BaseOrderCollapseViewHolder<T: BetSlipData>(protected val mBinding: ItemOrderSportBettingCollapseBinding): BaseViewHolder(mBinding) {

    private var lastClickTime = 0L

    abstract fun init(item: T)

    fun setDoubleClick(onDoubleClick: () -> Unit) {
        mBinding.root.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < BaseOrderViewHolder.COLLAPSE_ANIMATION_DURATION) {
                // 雙擊
                onDoubleClick.invoke()
                lastClickTime = 0L // 重置，避免三擊觸發
            } else {
                // 單擊
                lastClickTime = currentTime
            }
        }
    }
}