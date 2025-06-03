package arch.cayenne.module.home.data.repo

import androidx.room.Transaction
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
) : BaseMatchRepository(scope, socketManager, betDao, matchDao) {
    @Transaction
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
            matchDao.insertMatch(
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

    suspend fun getOnCurrentMatch(matchId: Long, selectedIds: List<Long>) : MatchWithMarkets? = matchDao.getOneMatchById(matchId).setSelected(betDao, selectedIds)
}