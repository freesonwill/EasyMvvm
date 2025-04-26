package arch.cayenne.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.BetDetailBean

class BetResultDetailCompare: DiffUtil.ItemCallback<BetDetailBean>() {
    override fun areItemsTheSame(
        oldItem: BetDetailBean,
        newItem: BetDetailBean
    ): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(
        oldItem: BetDetailBean,
        newItem: BetDetailBean
    ): Boolean {
        return oldItem == newItem
    }
}