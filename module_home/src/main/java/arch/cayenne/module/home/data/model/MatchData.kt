package arch.cayenne.module.home.data.model

import arch.cayenne.lib.database.entity.MatchListItem
import arch.cayenne.module.home.ui.adapter.OnMatchItemClickListener


data object MatchNoMoreData : MatchListItem

data object MatchLoadMoreData: MatchListItem

data class MatchDateItem(val dateStr: String, val timeStamp: Long) : MatchListItem

data class MatchQueryDateNoData(val leagueName: String, val timeStamp: Long, val clickListener: ()->Unit) : MatchListItem