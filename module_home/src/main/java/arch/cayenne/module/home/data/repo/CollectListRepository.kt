package arch.cayenne.module.home.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.extension.sendAndWaitProtoMessageResponse
import arch.cayenne.module.home.data.model.CollectMatchRef
import arch.cayenne.module.home.data.model.toRoomData
import arch.cayenne.module.home.utils.setSelected
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
) : BaseRepository() {

    private val collectMatchChange by lazy { MutableStateFlow<Map<Long, CollectMatchRef>>(hashMapOf()) }  //CollectMatchCrossRef

    suspend fun getCollectData(page: Int) : Boolean {
        val resp = socketManager.sendAndWaitProtoMessageResponse<Client.ListCollectResp>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.LISt_COLLECT,
        ) {
            Client.ListCollectReq.newBuilder().apply {
                this.page = page
                this.size = 3
            }.build()
        }
        if (resp.error == null && resp.data != null) {
            val matchFullData = resp.data!!.matchList.toRoomData()
            matchDao.insertMatch(
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
                marketCrossRef = matchFullData.matchMarketCrossRefs,
                marketSelectCrossRefs = matchFullData.marketSelectCrossRefs,
            )

            val map = resp.data!!.matchList.mapIndexed { index, match ->
                match.matchId to CollectMatchRef(match.matchId, page, page * 100 + index)
            }.toMap()
            collectMatchChange.value = collectMatchChange.value + map
            return true
        }
        return false
    }

    suspend fun queryFullMatches(matchIds: List<Long>, selectedIds: List<Long>? = null) : List<MatchWithMarkets> {
        val result = matchDao.getOneMatchByIds(matchIds).setSelected(betDao, selectedIds)
        return matchIds.mapNotNull { id -> result.find { it.match.matchId == id } }
    }

    fun clearCurrentMatch() {
        collectMatchChange.value = emptyMap()
    }

    fun observeMatchChange() : Flow<Map<Long, CollectMatchRef>> = collectMatchChange
}