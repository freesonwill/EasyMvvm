package arch.cayenne.module.home.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets

class MatchItemCompare : DiffUtil.ItemCallback<MatchWithMarkets>() {
    override fun areItemsTheSame(oldItem: MatchWithMarkets, newItem: MatchWithMarkets): Boolean {
        return oldItem.match.matchId == newItem.match.matchId
    }

    override fun areContentsTheSame(oldItem: MatchWithMarkets, newItem: MatchWithMarkets): Boolean {
//        val b1 = compareMatchIsSame(oldItem.match, newItem.match)
//        val b2 = compareLiveInfoIsSame(oldItem.match.liveInfo, newItem.match.liveInfo)
//        val b3 = oldItem.markets.size == newItem.markets.size
//        oldItem.markets.map { it.market.marketId } == newItem.markets.map { it.market.marketId }
        return oldItem == newItem

//        return true
    }
    override fun getChangePayload(oldItem: MatchWithMarkets, newItem: MatchWithMarkets): Any? {
        val diff = mutableSetOf<String>()
        val oldLiveInfo = oldItem.match.liveInfo
        val newLiveInfo = newItem.match.liveInfo

        if (oldLiveInfo.clock != newLiveInfo.clock) diff.add("clock")
        if (oldLiveInfo.score != newLiveInfo.score) diff.add("score")
        if (oldLiveInfo.viewerCount != newLiveInfo.viewerCount) diff.add("viewerCount")
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
                    oldSelection.trend != newSelection.trend
                ) {
                    diff.add("odds") // 如果有其中任何一個不同就記錄 odds
                    return@forEachIndexed
                }
            }
        }
        return if (diff.isEmpty()) null else diff
    }
}