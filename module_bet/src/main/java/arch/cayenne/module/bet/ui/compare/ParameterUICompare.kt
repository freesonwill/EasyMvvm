package arch.cayenne.module.bet.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.module.bet.ui.fragment.ComboDetailFragment.ParameterUIItem

/**
 * @date: 2025/12/11 17:13
 * @description:
 */
class ParameterUICompare : DiffUtil.ItemCallback<ParameterUIItem>() {
    override fun areItemsTheSame(oldItem: ParameterUIItem, newItem: ParameterUIItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(old: ParameterUIItem, new: ParameterUIItem): Boolean {
        return old == new
    }
}
