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
                newItem.trend == 0 &&
                oldItem.isSelected == newItem.isSelected
    }

    override fun getChangePayload(oldItem: SelectionBeanLite, newItem: SelectionBeanLite): Any? {
        val diff = mutableSetOf<String>()
        if (oldItem.odds != newItem.odds) diff.add("odds")
        if (oldItem.shortName != newItem.shortName) diff.add("shortName")
        if (oldItem.active != newItem.active) diff.add("active")
        if (oldItem.parlay != newItem.parlay) diff.add("parlay")
        if (newItem.trend != 0) {
            diff.add("trend")
        }
        if (oldItem.isSelected != newItem.isSelected) diff.add("isSelected")
        return if (diff.isEmpty()) null else diff
    }
}