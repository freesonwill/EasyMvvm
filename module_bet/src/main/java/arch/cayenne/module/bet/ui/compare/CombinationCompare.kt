package arch.cayenne.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.bet.data.ParameterItems

class CombinationCompare: DiffUtil.ItemCallback<ParameterItems>() {

    override fun areItemsTheSame(
        oldItem: ParameterItems,
        newItem: ParameterItems
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: ParameterItems,
        newItem: ParameterItems
    ): Boolean {
        return oldItem == newItem
    }
}