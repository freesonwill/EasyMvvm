package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.SelectionsEdit
import arch.cayenne.lib.websocket.data.ConnectState
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Client.MatchBasicUpdate
import galaxy.client.proto.Sloth
import galaxy.common.proto.Common.Market
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LiveMainRepository(
    private val remoteManager: LiveRemoteManager, private val database: GameDatabase
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeBalance(): Flow<InfoBean> = database.infoDao().observeBalance()
    fun observeMatchBean(matchId: Long) = database.liveMatchDao().observeMatchById(matchId)
    fun observeConnectStateFlow(): Flow<ConnectState> = remoteManager.getConnectStateFlow()

    // 500-1003: 获取比赛详情
    suspend fun getMatchRes(matchId: Long, callback: (LiveMatchBean) -> Unit) {
        clearMatchCache()
        var matchFullData = remoteManager.getMatchReq(scope, matchId)?.toRoomData()
        if (matchFullData != null) {
            matchFullData.match.find { it.matchId == matchId }
                ?.let { scope.launch(Dispatchers.Main) { callback(it) } }
            database.liveMatchDao().insertFullMatch(
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
                selectionsRecord = matchFullData.selectionsRecord,
            )
        }
    }

    private fun clearMatchCache() {
        database.liveMatchDao().clearAllMatch()
    }

    suspend fun registerMatchInfoNotify(matchId: Long) {
        remoteManager.registerMatchInfoNotify(scope, matchId)
    }

    suspend fun observeMatchInfoNotify() {
        remoteManager.observeMatchInfoNotify().collect {
            scope.launch(Dispatchers.IO) {
                updateFullMatchInfo(
                    if (it.hasBasicUpdate()) {
                        it.basicUpdate
                    } else {
                        null
                    }, it.marketUpdateList, it.matchId
                )
            }
        }
    }

    suspend fun updateFullMatchInfo(
        marketInfo: MatchBasicUpdate?, marketUpdate: List<Market>, matchId: Long
    ) {
        database.liveMatchDao().deleteSelectionsEdit()
        marketInfo?.let {
            if (marketInfo.hasLiveInfo()) {
                database.liveMatchDao().updateNotifyMatchInfo(
                    matchId = matchId,
                    status = marketInfo.status,
                    betStop = marketInfo.betStop,
                    startTime = marketInfo.startTime,
                    clock = marketInfo.liveInfo.clock,
                    rollClock = marketInfo.liveInfo.rollClock,
                    period = marketInfo.liveInfo.period,
                    score = marketInfo.liveInfo.score,
                    liveVideo = marketInfo.liveInfo.liveVideo,
                    charRoom = marketInfo.liveInfo.chatRoom,
                    viewerCount = marketInfo.liveInfo.viewerCount,
                    clockModified = marketInfo.liveInfo.clockModified,
                )
            } else {
                database.liveMatchDao().updateNotifyMatchInfo(
                    matchId = matchId,
                    status = marketInfo.status,
                    betStop = marketInfo.betStop,
                    startTime = marketInfo.startTime,
                )
            }
        }
        val selections =
            marketUpdate.selectionsToRoomData(database.liveMatchDao().getSelectionsRecord())
        database.liveMatchDao().updateLiveSelectionBean(
            selections.selectionsEdit,
            selections.selectionsRecord,
            selections.selectionsAdd,
            selections.selectionsDelete,
            selections.marketsAdd,
            selections.selectionsEditId
        )
    }

    fun unregisterMatchInfoNotify(matchId: Long) {
        remoteManager.unregisterMatchInfoNotify(scope, matchId)
    }

   suspend fun getSelectionsEdit() :List<SelectionsEdit>{
       return withContext(IO){
           database.liveMatchDao().getSelectionsEdit()
       }
    }

    fun clearAllMatch() {
        scope.launch(Dispatchers.IO) {
            database.liveMatchDao().clearAllMatch()
        }
    }

    suspend fun registerMatchStaticsNotify(matchId: Long): Sloth.MatchLiveData? {
        return remoteManager.registerMatchStaticsNotify(scope, matchId)
    }

    suspend fun unregisterStatisticsNotify() {
        remoteManager.unregisterMatchStaticsNotify(scope)
    }

    suspend fun observeMatchStaticsNotify(): Flow<Sloth.MatchLiveData> {
        return remoteManager.observeMatchStaticsNotify()
    }

    fun reconnect() {
        remoteManager.connectToServer()
    }
}

