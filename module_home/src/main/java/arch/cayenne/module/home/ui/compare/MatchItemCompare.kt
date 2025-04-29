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

    private fun compareLiveInfoIsSame(oldItem: MatchLiveInfoBean, newItem: MatchLiveInfoBean): Boolean {
        return oldItem.clock == newItem.clock &&
                oldItem.rollClock == newItem.rollClock &&
                oldItem.period == newItem.period &&
                oldItem.score == newItem.score &&
                oldItem.liveVideo == newItem.liveVideo &&
                oldItem.charRoom == newItem.charRoom &&
                oldItem.viewerCount == newItem.viewerCount &&
                oldItem.clockModified == newItem.clockModified
    }
    private fun compareMatchIsSame(oldItem: MatchBean, newItem: MatchBean): Boolean {
        return oldItem.matchId == newItem.matchId &&
                oldItem.basicInfo.status == newItem.basicInfo.status &&
                oldItem.basicInfo.betStop == newItem.basicInfo.betStop &&
                oldItem.basicInfo.startTime == newItem.basicInfo.startTime
    }
}