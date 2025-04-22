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
}