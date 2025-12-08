package arch.cayenne.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterItems2

class ComboDetailItemCompare : DiffUtil.ItemCallback<ParameterItems2>() {

    override fun areItemsTheSame(
        oldItem: ParameterItems2,
        newItem: ParameterItems2
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ParameterItems2,
        newItem: ParameterItems2
    ): Boolean {
        return oldItem == newItem
    }
}