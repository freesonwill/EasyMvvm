package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.LiveMatchDao
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Client
import galaxy.client.proto.Client.MatchBasicUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class LiveMainRepository(
    private val remoteManager: LiveRemoteManager, private val database: GameDatabase
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeBalance(): Flow<Long> = database.infoDao().observeBalance()
    fun observeMatchBean(matchId: Long) = database.liveMatchDao().observeMatchById(matchId)

    // 500-1003: 获取比赛详情
    suspend fun getMatchRes(matchId: Long) {
        clearMatchCache()
        var matchFullData = remoteManager.getMatchReq(scope, matchId)?.toRoomData()
        if (matchFullData != null) {
            database.liveMatchDao().insertFullMatch(
                matches = matchFullData.match,
                markets = matchFullData.markets,
                selections = matchFullData.selections,
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
            updateFullMatchInfo(it.basicUpdate, it.matchId)
        }
    }

    suspend fun updateFullMatchInfo(
        marketInfo: MatchBasicUpdate, matchId: Long
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
    }

    suspend fun unregisterMatchInfoNotify(matchId: Long) {
        remoteManager.unregisterMatchInfoNotify(scope, matchId)
    }

}

