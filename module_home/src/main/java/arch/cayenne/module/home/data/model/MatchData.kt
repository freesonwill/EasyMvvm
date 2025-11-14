package arch.cayenne.module.home.data.model

import arch.cayenne.lib.database.entity.MatchListItem


data object MatchNoMoreData : MatchListItem

data object MatchLoadMoreData: MatchListItem

data class MatchDateItem(val dateStr: String) : MatchListItem