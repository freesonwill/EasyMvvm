package arch.cayenne.module.home.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.SelectionBean

class OddsDiffCallback : DiffUtil.ItemCallback<List<SelectionBean>>() {

    override fun areItemsTheSame(
        oldItem: List<SelectionBean>,
        newItem: List<SelectionBean>
    ): Boolean {
        return oldItem.map { it.selectionId } == newItem.map { it.selectionId }
    }

    override fun areContentsTheSame(
        oldItem: List<SelectionBean>,
        newItem: List<SelectionBean>
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
        oldItem: List<SelectionBean>,
        newItem: List<SelectionBean>
    ): Any? {
        val diff = mutableSetOf<String>()
        oldItem.zip(newItem).forEach { (old, new) ->
            if (old.odds != new.odds) diff.add("odds")
            if (old.shortName != new.shortName) diff.add("shortName")
            if (old.active != new.active) diff.add("active")
            if (old.parlay != new.parlay) diff.add("parlay")
        }
        return if (diff.isEmpty()) null else diff
    }
}
