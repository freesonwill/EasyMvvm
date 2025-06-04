package arch.cayenne.module.home.data.repo

import androidx.room.Transaction
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.model.CollectMatchRef
import arch.cayenne.module.home.data.model.toRoomData
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CollectListRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val betDao: BetDao,
    private val matchDao: MatchDao,
) : BaseMatchRepository(scope, socketManager, betDao, matchDao) {

    private val collectMatchChange by lazy { MutableStateFlow<Map<Long, CollectMatchRef>>(hashMapOf()) }  //CollectMatchCrossRef

    suspend fun getCollectData(page: Int) : Boolean {

        val last = collectMatchChange.value.maxByOrNull { it.value.order }?.value
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.ListCollectResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LISt_COLLECT,
        ) {
            Client.ListCollectReq.newBuilder().apply {
                if (last != null) {
                    this.cursorMatchId = last.matchId
                    this.cursorMatchStartTime = last.startTime
                }
                this.size = DEFAULT_MATCH_SIZE
            }.build()
        }
        if (resp.error == null && resp.data != null) {
            if (resp.data!!.matchList.isNullOrEmpty()) {
                return false
            }
            val matchFullData = resp.data!!.matchList.toRoomData()
            matchDao.insertMatch(
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
                marketCrossRef = matchFullData.matchMarketCrossRefs,
                marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
            )

            val map = resp.data!!.matchList.mapIndexed { index, match ->
                match.matchId to CollectMatchRef(match.matchId, match.basicInfo.startTime, page, page * 100 + index)
            }.toMap()
            collectMatchChange.value = collectMatchChange.value + map
            return true
        }
        return false
    }

    fun clearCurrentMatch() {
        collectMatchChange.value = emptyMap()
    }

    @Transaction
    suspend fun removeMatchCollect(item: MatchWithMarkets) {
        if (matchCollect(item, false) != null) {
            collectMatchChange.value = collectMatchChange.value - item.match.matchId
        }
    }

    fun observeMatchChange() : Flow<Map<Long, CollectMatchRef>> = collectMatchChange
}