package arch.cayenne.module.home.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.SelectionBeanLite

class OddsDiffCallback : DiffUtil.ItemCallback<List<SelectionBeanLite>>() {

    override fun areItemsTheSame(
        oldItem: List<SelectionBeanLite>,
        newItem: List<SelectionBeanLite>
    ): Boolean {
        return oldItem.map { it.selectionId } == newItem.map { it.selectionId }
    }

    override fun areContentsTheSame(
        oldItem: List<SelectionBeanLite>,
        newItem: List<SelectionBeanLite>
    ): Boolean {
        if (oldItem.size != newItem.size) return false
        return oldItem.zip(newItem).all { (oldSelection, newSelection) ->
            oldSelection.selectionId == newSelection.selectionId &&
                    oldSelection.shortName == newSelection.shortName &&
                    oldSelection.odds == newSelection.odds &&
                    oldSelection.active == newSelection.active &&
                    oldSelection.parlay == newSelection.parlay
        }
    }

    override fun getChangePayload(
        oldItem: List<SelectionBeanLite>,
        newItem: List<SelectionBeanLite>
    ): Any? {
        val diff = mutableSetOf<String>()
        oldItem.zip(newItem).forEach { (old, new) ->
            if (old.odds != new.odds) diff.add("odds")
            if (old.shortName != new.shortName) diff.add("shortName")
            if (old.active != new.active) diff.add("active")
            if (old.parlay != new.parlay) diff.add("parlay")
            if (old.trend != new.trend) diff.add("trend")
            if (old.isSelected != new.isSelected) diff.add("isSelected")
        }
        return if (diff.isEmpty()) null else diff
    }
}
