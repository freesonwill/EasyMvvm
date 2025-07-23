package com.walisport.module.search.data.transformer

import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.model.SearchDailyMatchBean
import com.walisport.module.search.data.model.SearchMatchBean
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.data.model.SearchResultTeamBean
import com.walisport.module.search.data.model.SearchResultTournamentBean
import galaxy.client.proto.Client

object SearchTransformer {
    fun Client.SearchResp.toSearchResultBean(): SearchResultBean {
        return when (SearchResultTypeEnum.fromCode(this.type)) {
            SearchResultTypeEnum.LIST -> {
                SearchResultBean(
                    type = SearchResultTypeEnum.LIST,
                    dataList = listOfNotNull(
                        dataList?.tournamentList?.let { SearchResultTournamentBean.fromList(it) },
                        dataList?.teamList?.let { SearchResultTeamBean.fromList(it) },
                        dataList?.playerList?.let { SearchResultPlayerBean.fromList(it) }
                    ).flatten()
                )
            }

            SearchResultTypeEnum.PLAYER -> {
                SearchResultBean(
                    type = SearchResultTypeEnum.PLAYER,
                    directData = player?.let { SearchResultPlayerBean.from(it) },
                    matchTotal = matchTotal,
                    matches = matchesList?.let { SearchMatchBean.fromList(it) },
                    dailyCount = dailyCountList?.let { SearchDailyMatchBean.fromList(it) }
                )
            }

            SearchResultTypeEnum.TEAM -> {
                SearchResultBean(
                    type = SearchResultTypeEnum.TEAM,
                    directData = team?.let { SearchResultTeamBean.from(it) },
                    matchTotal = matchTotal,
                    matches = matchesList?.let { SearchMatchBean.fromList(it) },
                    dailyCount = dailyCountList?.let { SearchDailyMatchBean.fromList(it) }
                )
            }

            SearchResultTypeEnum.TOURNAMENT -> {
                SearchResultBean(
                    type = SearchResultTypeEnum.TOURNAMENT,
                    directData = tournament?.let { SearchResultTournamentBean.from(it) },
                    matchTotal = matchTotal,
                    matches = matchesList?.let { SearchMatchBean.fromList(it) },
                    dailyCount = dailyCountList?.let { SearchDailyMatchBean.fromList(it) }
                )
            }

            else -> SearchResultBean(type = SearchResultTypeEnum.NONE)
        }
    }
}