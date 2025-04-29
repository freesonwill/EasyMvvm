package arch.cayenne.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetSelectionBean

class BetSelectionBeanCompare: DiffUtil.ItemCallback<BetSelectionBean>() {
    override fun areItemsTheSame(oldItem: BetSelectionBean, newItem: BetSelectionBean): Boolean {
        return oldItem.selectionId == newItem.selectionId
    }

    override fun areContentsTheSame(oldItem: BetSelectionBean, newItem: BetSelectionBean): Boolean {
        return oldItem == newItem
    }
}