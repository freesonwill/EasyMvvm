package arch.cayenne.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.bet.data.ComboMultiBetBean

class ComboRateCompare: DiffUtil.ItemCallback<ComboMultiBetBean>() {
    override fun areItemsTheSame(oldItem: ComboMultiBetBean, newItem: ComboMultiBetBean): Boolean {
        return oldItem.serialValue == newItem.serialValue // 判斷唯一 ID
    }

    override fun areContentsTheSame(oldItem: ComboMultiBetBean, newItem: ComboMultiBetBean): Boolean {
        return oldItem == newItem // 這裡要小心，可能會導致畫面不刷新
    }

    override fun getChangePayload(oldItem: ComboMultiBetBean, newItem: ComboMultiBetBean): Any? {
        return if (oldItem.inputMoney != newItem.inputMoney) {
            "MONEY_CHANGED"
        } else null
    }
}