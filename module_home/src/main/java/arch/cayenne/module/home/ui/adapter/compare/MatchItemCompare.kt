package arch.cayenne.module.home.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.MatchListItem
import arch.cayenne.lib.database.entity.MatchWithMarkets

class MatchItemCompare : DiffUtil.ItemCallback<MatchListItem>() {
    override fun areItemsTheSame(oldItem: MatchListItem, newItem: MatchListItem): Boolean {
        return if (oldItem is MatchWithMarkets && newItem is MatchWithMarkets) {
            oldItem.match.matchId == newItem.match.matchId
        } else {
            oldItem == newItem
        }
    }

    override fun areContentsTheSame(oldItem: MatchListItem, newItem: MatchListItem): Boolean {
        return if (oldItem is MatchWithMarkets && newItem is MatchWithMarkets){
            oldItem == newItem
        } else true
    }
    override fun getChangePayload(oldItem: MatchListItem, newItem: MatchListItem): Any? {
        if (oldItem !is MatchWithMarkets || newItem !is MatchWithMarkets) return mutableSetOf<String>()
        val diff = mutableSetOf<String>()
        val oldLiveInfo = oldItem.match.liveInfo
        val newLiveInfo = newItem.match.liveInfo

        if (oldItem.match.basicInfo.status != newItem.match.basicInfo.status) diff.add("status")
        if (oldLiveInfo.clock != newLiveInfo.clock) diff.add("clock")
        if (oldLiveInfo.rollClock != newLiveInfo.rollClock) diff.add("rollClock")
        if (oldLiveInfo.score != newLiveInfo.score) diff.add("score")
        if (oldLiveInfo.viewerCount != newLiveInfo.viewerCount) diff.add("viewerCount")
        if (oldLiveInfo.liveAnimation != newLiveInfo.liveAnimation) diff.add("liveAnimation")
        if (oldLiveInfo.liveVideo != newLiveInfo.liveVideo) diff.add("liveVideo")
        if (oldItem.match.collect != newItem.match.collect) diff.add("collect")

        // 遍歷比對所有 MarketWithSelections
        oldItem.markets.forEachIndexed { index, oldMarket ->
            val newMarket = newItem.markets.getOrNull(index) ?: return@forEachIndexed

            oldMarket.selections.forEachIndexed { selIndex, oldSelection ->
                val newSelection = newMarket.selections.getOrNull(selIndex) ?: return@forEachIndexed

                if (oldSelection.odds != newSelection.odds ||
                    oldSelection.active != newSelection.active ||
                    oldSelection.shortName != newSelection.shortName ||
                    oldSelection.parlay != newSelection.parlay ||
                    oldSelection.isSelected != newSelection.isSelected ||
                    newSelection.trend != 0
                ) {
                    diff.add("odds") // 如果有其中任何一個不同就記錄 odds
                    return@forEachIndexed
                }
            }
        }
        return if (diff.isEmpty()) null else diff
    }
}