package arch.cayenne.module.home.ui.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.MatchWithMarkets

class MatchItemCompare : DiffUtil.ItemCallback<MatchWithMarkets>() {
    override fun areItemsTheSame(oldItem: MatchWithMarkets, newItem: MatchWithMarkets): Boolean {
        return oldItem.match.matchId == newItem.match.matchId
    }

    override fun areContentsTheSame(oldItem: MatchWithMarkets, newItem: MatchWithMarkets): Boolean {
        return oldItem == newItem
    }
    override fun getChangePayload(oldItem: MatchWithMarkets, newItem: MatchWithMarkets): Any? {
        val diff = mutableSetOf<String>()
        val oldLiveInfo = oldItem.match.liveInfo
        val newLiveInfo = newItem.match.liveInfo
        if (oldLiveInfo.clock != newLiveInfo.clock) diff.add("clock")
        if (oldLiveInfo.score != newLiveInfo.score) diff.add("score")
        if (oldLiveInfo.viewerCount != newLiveInfo.viewerCount) diff.add("viewerCount")

        val oldSelections = oldItem.markets.firstOrNull()?.selections.orEmpty()
        val newSelections = newItem.markets.firstOrNull()?.selections.orEmpty()
        if (oldSelections != newSelections) diff.add("odds")
        return if (diff.isEmpty()) null else diff
    }
}