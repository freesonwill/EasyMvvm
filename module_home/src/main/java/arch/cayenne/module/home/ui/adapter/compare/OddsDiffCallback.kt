package arch.cayenne.module.home.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.SelectionBeanLite

class OddsDiffCallback : DiffUtil.ItemCallback<Pair<MarketBeanLite, List<SelectionBeanLite>>>() {

    override fun areItemsTheSame(
        oldItem: Pair<MarketBeanLite, List<SelectionBeanLite>>,
        newItem: Pair<MarketBeanLite, List<SelectionBeanLite>>
    ): Boolean {
        return oldItem.first.marketId == newItem.first.marketId &&
                oldItem.second.map { it.selectionId } == newItem.second.map { it.selectionId }
    }

    override fun areContentsTheSame(
        oldItem: Pair<MarketBeanLite, List<SelectionBeanLite>>,
        newItem: Pair<MarketBeanLite, List<SelectionBeanLite>>
    ): Boolean {
        val (oldMarket, oldSelections) = oldItem
        val (newMarket, newSelections) = newItem

        if (oldMarket.marketId != newMarket.marketId) return false
        if (oldMarket.defaultSelectionCount != newMarket.defaultSelectionCount) return false

        if (oldSelections.size != newSelections.size) return false
        return oldSelections.indices.all { index ->
            oldSelections[index] == newSelections[index]
        }
    }

    override fun getChangePayload(
        oldItem: Pair<MarketBeanLite, List<SelectionBeanLite>>,
        newItem: Pair<MarketBeanLite, List<SelectionBeanLite>>
    ): Any? {
        val (oldMarket, oldSelections) = oldItem
        val (newMarket, newSelections) = newItem

        val diff = mutableSetOf<String>()

        if (oldMarket.defaultSelectionCount != newMarket.defaultSelectionCount) {
            diff.add("defaultSelectionCount")
        }

        oldSelections.forEachIndexed { index, old ->
            val new = newSelections[index]
            if (old.odds != new.odds) diff.add("odds")
            if (old.shortName != new.shortName) diff.add("shortName")
            if (old.active != new.active) diff.add("active")
            if (old.parlay != new.parlay) diff.add("parlay")
            if (new.trend != 0) {
                diff.add("trend")
            }
            if (old.isSelected != new.isSelected) diff.add("isSelected")
        }
        return if (diff.isEmpty()) null else diff
    }
}
