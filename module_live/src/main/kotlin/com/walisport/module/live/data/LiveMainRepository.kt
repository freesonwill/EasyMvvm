package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.LiveMatchBean
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Client.MatchBasicUpdate
import galaxy.common.proto.Common.Market
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LiveMainRepository(
    private val remoteManager: LiveRemoteManager, private val database: GameDatabase
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeBalance(): Flow<Long> = database.infoDao().observeBalance()
    fun observeMatchBean(matchId: Long) = database.liveMatchDao().observeMatchById(matchId)

    // 500-1003: 获取比赛详情
    suspend fun getMatchRes(matchId: Long,callback: (LiveMatchBean) -> Unit) {
            clearMatchCache()
            var matchFullData = remoteManager.getMatchReq(scope, matchId)?.toRoomData()
            if (matchFullData != null) {
                matchFullData.match.find { it.matchId==matchId }?.let { scope.launch(Dispatchers.Main){callback(it)} }
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
            scope.launch{
                updateFullMatchInfo(it.basicUpdate,  it.marketUpdateList,it.matchId)
            }
        }
    }

    suspend fun updateFullMatchInfo(
        marketInfo: MatchBasicUpdate, marketUpdate: List<Market>, matchId: Long
    ) {
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
        var selections = marketUpdate.selectionsToRoomData(database.liveMatchDao().getSelectionsRecord())
        database.liveMatchDao().updateLiveSelectionBean(selections =selections.selections,selections.selectionsRecord )
    }

     fun unregisterMatchInfoNotify(matchId: Long) {
        remoteManager.unregisterMatchInfoNotify(scope, matchId)
    }

     fun clearAllMatch(){
        scope.launch(Dispatchers.IO){
            database.clearAllTables()
        }
    }

}

