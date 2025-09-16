package com.walisport.app.data

import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketDetailBean
import arch.cayenne.lib.database.entity.MarketSelectCrossRef
import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.MatchMarketCrossRef
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.module.home.data.model.MatchFullData

data class PreloadDataModel(
    val statistical: List<PlayTypeItem>,
    val tournament: List<Tournament>,
    val match: List<Match>,
)

data class PlayTypeItem(
    val playType: Int,
    val matchCount: Int? = null,
    val sportStatistical: List<SportStatisticalItem>,
    val playName: String
)

data class SportStatisticalItem(
    val sportId: Int,
    val sportName: String,
    val matchCount: Int? = null
)

data class Tournament(
    val id: Int,
    val name: String,
    val simpleName: String,
    val icon: String?,
    val sportId: Int,
    val hot: Boolean = false,
    val weight: Int = 0,
)

data class Match(
    val matchId: Long,
    val collect: Boolean,
    val basicInfo: MatchBasicInfo,
    val market: List<Market>,
)

data class MatchBasicInfo(
    val matchId: Long,
    val matchName: String,
    val homeTeam: String,
    val homeTeamId: Int,
    val homeTeamIcon: String?,
    val awayTeam: String,
    val awayTeamId: Int,
    val awayTeamIcon: String?,
    val startTime: Long,
    val status: Int,
    val tournamentId: Int,
    val tournamentName: String,
    val tournamentShortName: String,
    val tournamentIcon: String?,
    val sportId: Int,
    val sportName: String,
    val liveInfo: LiveInfo,
    val matchType: Int,
    val provider: Int,
    val betStop: Boolean = false,
    val tournamentHot: Boolean = false,
    val tournamentWeight: Int = 0,
)

data class LiveInfo(
    val clock: Int = 0,
    val rollClock: Boolean = false,
    val period: Int,
    val score: String,
    val liveVideo: Boolean = false,
    val animationLiveUrl: String?,
    val chatRoom: Boolean,
    val viewerCount: Int = 0,
    val clockModified: Long,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val periodName: String?
)

data class Market(
    val marketId: Long,
    val marketType: Int,
    val marketPeriod: Int,
    val marketName: String,
    val marketDetail: List<MarketDetail>,
    val style: Int,
    val status: Int = 0,
    val marketCategory: List<Int>,
    val weight: Int = 0,
)

data class MarketDetail(
    val specifier: String? = null,
    val selection: List<Selection>?,
    val active: Boolean,
    val parlay: Boolean
)

data class Selection(
    val selectionId: Long,
    val name: String,
    val shortName: String,
    val odds: String,
    val active: Boolean,
    val parlay: Boolean
)

fun List<Match>.toRoomData() : MatchFullData {
    val matches = mutableListOf<MatchBean>()
    val markets = mutableListOf<MarketBean>()
    val selections = mutableListOf<SelectionBean>()
    val matchMarketCrossRefs = mutableListOf<MatchMarketCrossRef>()
    val marketSelectCrossRef = mutableListOf<MarketSelectCrossRef>()
    this.forEach { match ->
        val matchId = match.matchId
        matches.add(
            MatchBean(
                matchId = match.matchId,
                collect = match.collect,
                basicInfo = MatchBasicInfoBean(
                    matchId = match.basicInfo.matchId,
                    matchName = match.basicInfo.matchName,
                    homeTeam = match.basicInfo.homeTeam,
                    homeTeamId = match.basicInfo.homeTeamId,
                    homeTeamIcon = match.basicInfo.homeTeamIcon?:"",
                    awayTeam = match.basicInfo.awayTeam,
                    awayTeamId = match.basicInfo.awayTeamId,
                    awayTeamIcon = match.basicInfo.awayTeamIcon?:"",
                    startTime = match.basicInfo.startTime,
                    status = match.basicInfo.status,
                    tournamentId = match.basicInfo.tournamentId,
                    tournamentName = match.basicInfo.tournamentName,
                    tournamentShortName = match.basicInfo.tournamentShortName,
                    tournamentIcon = match.basicInfo.tournamentIcon?:"",
                    sportId = match.basicInfo.sportId,
                    sportName = match.basicInfo.sportName,
                    betStop = match.basicInfo.betStop,
                    tournamentHot = match.basicInfo.tournamentHot,
                    tournamentWeight = match.basicInfo.tournamentWeight,
                    provider = match.basicInfo.provider
                ),
                liveInfo = MatchLiveInfoBean(
                    clock = match.basicInfo.liveInfo.clock,
                    rollClock = match.basicInfo.liveInfo.rollClock,
                    period = match.basicInfo.liveInfo.periodName?:"",
                    score = match.basicInfo.liveInfo.score,
                    liveVideo = match.basicInfo.liveInfo.liveVideo,
                    charRoom = match.basicInfo.liveInfo.chatRoom,
                    viewerCount = match.basicInfo.liveInfo.viewerCount,
                    clockModified = match.basicInfo.liveInfo.clockModified,
                    homeScore = match.basicInfo.liveInfo.homeScore,
                    awayScore = match.basicInfo.liveInfo.awayScore,
                    liveAnimation = match.basicInfo.liveInfo.animationLiveUrl?:""
                )
            )
        )
        match.market.forEachIndexed { index, market ->
            val marketId = market.marketId
            markets.add(
                MarketBean(
                    marketId = market.marketId,
                    marketName = market.marketName,
                    status = market.status
                )
            )

            var selectionCount = 0
            market.marketDetail.forEachIndexed { index, detail ->
                selectionCount += detail.selection?.size ?: 0
                detail.selection?.filter { it.selectionId != 0L }?.forEachIndexed { selectionIndex, selection ->
                    val selectionId = selection.selectionId
                    selections.add(
                        SelectionBean(
                            selectionId = selection.selectionId,
                            detail = MarketDetailBean(
                                detailId = index,
                                specifier = detail.specifier,
                                active = detail.active,
                                parlay = detail.parlay,
                            ),
                            name = selection.name,
                            shortName = selection.shortName,
                            odds = selection.odds.toOdds(),
                            active = selection.active,
                            parlay = selection.parlay,
                        )
                    )
                    marketSelectCrossRef.add(MarketSelectCrossRef(matchId, marketId, selectionId, selectionIndex))
                }
            }
            matchMarketCrossRefs.add(
                MatchMarketCrossRef(matchId,marketId, selectionCount, index)
            )
        }
    }
    return MatchFullData(
        matches,
        markets,
        selections,
        matchMarketCrossRefs,
        marketSelectCrossRef,
    )
}
