package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.lib.database.entity.MatchBasicInfoBean
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchLiveInfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CollectListRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
) : BaseRepository() {

    suspend fun getCollectData(page: Int) : List<MatchWithMarkets> {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.ListCollectResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LISt_COLLECT,
        ) {
            Client.ListCollectReq.newBuilder().apply {
                this.page = page
                this.size = 10
            }.build()
        }
        if (resp.error == null && resp.data != null) {
            val list = arrayListOf<MatchWithMarkets>()
            resp.data!!.matchList.forEach { originMatch ->
                val markets = arrayListOf<MarketWithSelections>()
                originMatch.marketList.forEach {  originMarket ->
                    val selections = arrayListOf<SelectionBeanLite>()
                    originMarket.marketDetailList.forEach { originMarketDetail ->
                        originMarketDetail.selectionList.forEach { originSelection ->
                            selections.add(
                                SelectionBeanLite(
                                    selectionId = originSelection.selectionId,
                                    detailActive = originMarketDetail.active,
                                    matchId = originMatch.matchId,
                                    name = originSelection.name,
                                    shortName = originSelection.shortName,
                                    odds = originSelection.odds.toOdds(),
                                    active = originSelection.active,
                                    parlay = originSelection.parlay,
                                )
                            )
                        }
                    }
                    markets.add(
                        MarketWithSelections(
                            market = MarketBeanLite(
                                marketId = originMarket.marketId,
                                marketName = originMarket.marketName,
                                status = originMarket.status,
                                defaultSelectionCount = originMarket.marketDetailList.flatMap { it.selectionList }.count()
                            ),
                            selections = selections
                        )
                    )
                }
                list.add(
                    MatchWithMarkets(
                        match = MatchBean(
                            matchId = originMatch.matchId,
                            collect = originMatch.collect,
                            basicInfo = MatchBasicInfoBean(
                                matchId = originMatch.basicInfo.matchId,
                                matchName = originMatch.basicInfo.matchName,
                                homeTeam = originMatch.basicInfo.homeTeam,
                                homeTeamId = originMatch.basicInfo.homeTeamId,
                                homeTeamIcon = originMatch.basicInfo.homeTeamIcon,
                                awayTeam = originMatch.basicInfo.awayTeam,
                                awayTeamId = originMatch.basicInfo.awayTeamId,
                                awayTeamIcon = originMatch.basicInfo.awayTeamIcon,
                                startTime = originMatch.basicInfo.startTime,
                                status = originMatch.basicInfo.status,
                                tournamentId = originMatch.basicInfo.tournamentId,
                                tournamentName = originMatch.basicInfo.tournamentName,
                                tournamentShortName = originMatch.basicInfo.tournamentShortName,
                                tournamentIcon = originMatch.basicInfo.tournamentIcon,
                                sportId = originMatch.basicInfo.sportId,
                                sportName = originMatch.basicInfo.sportName,
                                betStop = originMatch.basicInfo.betStop,
                                tournamentHot = originMatch.basicInfo.tournamentHot,
                                tournamentWeight = originMatch.basicInfo.tournamentWeight
                            ),
                            liveInfo = MatchLiveInfoBean(
                                clock = originMatch.basicInfo.liveInfo.clock,
                                rollClock = originMatch.basicInfo.liveInfo.rollClock,
                                period = originMatch.basicInfo.liveInfo.period,
                                score = originMatch.basicInfo.liveInfo.score,
                                liveVideo = originMatch.basicInfo.liveInfo.liveVideo,
                                charRoom = originMatch.basicInfo.liveInfo.chatRoom,
                                viewerCount = originMatch.basicInfo.liveInfo.viewerCount,
                                clockModified = originMatch.basicInfo.liveInfo.clockModified
                            )
                        ),
                        markets = markets
                    )

                )
            }
            return list
        }
        return arrayListOf()
    }
}