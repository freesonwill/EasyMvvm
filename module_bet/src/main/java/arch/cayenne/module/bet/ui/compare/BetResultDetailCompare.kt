package arch.cayenne.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.BetResultDetailBean

class BetResultDetailCompare: DiffUtil.ItemCallback<BetResultDetailBean>() {
    override fun areItemsTheSame(
        oldItem: BetResultDetailBean,
        newItem: BetResultDetailBean
    ): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(
        oldItem: BetResultDetailBean,
        newItem: BetResultDetailBean
    ): Boolean {
        return oldItem == newItem
    }
}