package arch.cayenne.module.home.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.MarketWithSelections

class ChampionItemCompare : DiffUtil.ItemCallback<MarketWithSelections>() {
    override fun areItemsTheSame(oldItem: MarketWithSelections, newItem: MarketWithSelections): Boolean {
        return oldItem.market.marketId == newItem.market.marketId
    }

    override fun areContentsTheSame(oldItem: MarketWithSelections, newItem: MarketWithSelections): Boolean {
        val oldMarket = oldItem.market
        val oldSelections = oldItem.selections
        val newMarket = newItem.market
        val newSelections = newItem.selections

        if (oldMarket.marketId != newMarket.marketId) return false
        if (oldMarket.defaultSelectionCount != newMarket.defaultSelectionCount) return false

        if (oldSelections.size != newSelections.size) return false

        return oldSelections.zip(newSelections).all { (oldSel, newSel) ->
            oldSel.selectionId == newSel.selectionId &&
                    oldSel.shortName == newSel.shortName &&
                    oldSel.odds == newSel.odds &&
                    oldSel.active == newSel.active &&
                    oldSel.parlay == newSel.parlay &&
                    oldSel.trend == newSel.trend &&
                    oldSel.isSelected == newSel.isSelected
        }
    }
    override fun getChangePayload(oldItem: MarketWithSelections, newItem: MarketWithSelections): Any? {
        val oldMarket = oldItem.market
        val oldSelections = oldItem.selections
        val newMarket = newItem.market
        val newSelections = newItem.selections
        val diff = mutableSetOf<String>()

        if (oldMarket.defaultSelectionCount != newMarket.defaultSelectionCount) {
            diff.add("defaultSelectionCount")
        }

        oldSelections.zip(newSelections).forEach { (old, new) ->
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