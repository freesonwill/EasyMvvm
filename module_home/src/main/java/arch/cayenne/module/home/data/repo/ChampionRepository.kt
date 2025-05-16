package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.model.toRoomData
import arch.cayenne.module.home.utils.setSelected
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ChampionRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val matchDao: MatchDao,
    private val betDao: BetDao
) : BaseRepository() {

    suspend fun getChampionDetail(matchId: Long) : MatchWithMarkets? {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.GetMatchResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.GET_MATCH,
        ){
            Client.GetMatchReq.newBuilder().apply {
                this.matchId = matchId
            }.build()
        }

        if (resp.error == null && resp.data != null) {
            val matchFullData = listOf(resp.data!!.match).toRoomData()
            "KC_ ${resp.data!!.match.basicInfo.matchName}".logi()
            "KC_ ${resp.data!!.match.marketList}".logi()
            matchDao.insertFullMatch(
                tournamentMatchRefs = null,
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
                marketCrossRef = matchFullData.matchMarketCrossRefs,
                marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
            )
            return matchDao.getOneMatchById(matchId).setSelected(betDao)
        }
        return null
    }
}