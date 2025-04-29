package arch.cayenne.module.home.data

import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketDetailBean
import arch.cayenne.lib.database.entity.MarketSelectCrossRef
import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchBeanLite
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.MatchMarketCrossRef
import arch.cayenne.lib.database.entity.SelectionBean
import galaxy.client.proto.Client
import galaxy.common.proto.Common

data class MatchFullData(
    val match: List<MatchBean>,
    val markets: List<MarketBean>,
    val selections: List<SelectionBean>,
    val matchMarketCrossRefs: List<MatchMarketCrossRef>,
    val marketSelectCrossRefs: List<MarketSelectCrossRef>,
)

fun List<Common.Match>.toRoomData() : MatchFullData {
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
                    homeTeamIcon = match.basicInfo.homeTeamIcon,
                    awayTeam = match.basicInfo.awayTeam,
                    awayTeamId = match.basicInfo.awayTeamId,
                    awayTeamIcon = match.basicInfo.awayTeamIcon,
                    startTime = match.basicInfo.startTime,
                    status = match.basicInfo.status,
                    tournamentId = match.basicInfo.tournamentId,
                    tournamentName = match.basicInfo.tournamentName,
                    tournamentShortName = match.basicInfo.tournamentShortName,
                    tournamentIcon = match.basicInfo.tournamentIcon,
                    sportId = match.basicInfo.sportId,
                    sportName = match.basicInfo.sportName,
                    betStop = match.basicInfo.betStop,
                    tournamentHot = match.basicInfo.tournamentHot,
                    tournamentWeight = match.basicInfo.tournamentWeight
                ),
                liveInfo = MatchLiveInfoBean(
                    clock = match.basicInfo.liveInfo.clock,
                    rollClock = match.basicInfo.liveInfo.rollClock,
                    period = match.basicInfo.liveInfo.period,
                    score = match.basicInfo.liveInfo.score,
                    liveVideo = match.basicInfo.liveInfo.liveVideo,
                    charRoom = match.basicInfo.liveInfo.chatRoom,
                    viewerCount = match.basicInfo.liveInfo.viewerCount,
                    clockModified = match.basicInfo.liveInfo.clockModified
                )
            )
        )
        match.marketList.forEach { market ->
            val marketId = market.marketId
            markets.add(
                MarketBean(
                    marketId = market.marketId,
                    marketName = market.marketName,
                    status = market.status
                )
            )
            matchMarketCrossRefs.add(
                MatchMarketCrossRef(matchId,marketId)
            )
            market.marketDetailList.forEachIndexed { index, detail ->
                detail.selectionList.forEach { selection ->
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
                    marketSelectCrossRef.add(MarketSelectCrossRef(matchId, marketId, selectionId))
                }
            }
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

data class MatchUpdateData(
    val matchLites: List<MatchBeanLite>,
    val markets: List<MarketBean>,
    val selections: List<SelectionBean>,
    val matchMarketCrossRefs: List<MatchMarketCrossRef>,
    val marketSelectCrossRefs: List<MarketSelectCrossRef>,
)

fun List<Client.MatchNotify>.toRoomData() : MatchUpdateData {
    val matchLites = arrayListOf<MatchBeanLite>()
    val markets = mutableListOf<MarketBean>()
    val selections = mutableListOf<SelectionBean>()
    val matchMarketCrossRefs = mutableListOf<MatchMarketCrossRef>()
    val marketSelectCrossRef = mutableListOf<MarketSelectCrossRef>()
    this.forEach { matchNotify ->
        val matchId = matchNotify.matchId
        matchLites.add(
            MatchBeanLite(
                matchId = matchNotify.matchId,
                status = matchNotify.basicUpdate.status,
                betStop = matchNotify.basicUpdate.betStop,
                startTime = matchNotify.basicUpdate.startTime,
                liveInfo = MatchLiveInfoBean(
                    clock = matchNotify.basicUpdate.liveInfo.clock,
                    rollClock = matchNotify.basicUpdate.liveInfo.rollClock,
                    period = matchNotify.basicUpdate.liveInfo.period,
                    score = matchNotify.basicUpdate.liveInfo.score,
                    liveVideo = matchNotify.basicUpdate.liveInfo.liveVideo,
                    charRoom = matchNotify.basicUpdate.liveInfo.chatRoom,
                    viewerCount = matchNotify.basicUpdate.liveInfo.viewerCount,
                    clockModified = matchNotify.basicUpdate.liveInfo.clockModified
                )
            )
        )
        matchNotify.homeMarketUpdateList.forEach { market ->
            val marketId = market.marketId
            markets.add(
                MarketBean(
                    marketId = market.marketId,
                    marketName = market.marketName,
                    status = market.status
                )
            )
            matchMarketCrossRefs.add(
                MatchMarketCrossRef(matchId,marketId)
            )
            market.marketDetailList.forEachIndexed { index, detail ->
                detail.selectionList.forEach { selection ->
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
                    marketSelectCrossRef.add(MarketSelectCrossRef(matchId, marketId, selectionId))
                }
            }
        }
    }
    return MatchUpdateData(
        matchLites,
        markets,
        selections,
        matchMarketCrossRefs,
        marketSelectCrossRef,
    )
}