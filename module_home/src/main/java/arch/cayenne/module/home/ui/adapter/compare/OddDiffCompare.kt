package arch.cayenne.module.home.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.SelectionBeanLite

class OddDiffCompare : DiffUtil.ItemCallback<SelectionBeanLite>(){
    override fun areItemsTheSame(oldItem: SelectionBeanLite, newItem: SelectionBeanLite): Boolean {
        return oldItem.selectionId == newItem.selectionId
    }

    override fun areContentsTheSame(
        oldItem: SelectionBeanLite,
        newItem: SelectionBeanLite
    ): Boolean {
        return oldItem.selectionId == newItem.selectionId &&
                oldItem.shortName == newItem.shortName &&
                oldItem.odds == newItem.odds &&
                oldItem.active == newItem.active &&
                oldItem.parlay == newItem.parlay &&
                oldItem.trend == newItem.trend &&
                oldItem.isSelected == newItem.isSelected
    }
}