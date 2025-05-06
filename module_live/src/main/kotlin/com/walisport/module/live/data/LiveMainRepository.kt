package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.dao.LiveMatchDao
import com.walisport.module.live.LiveRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class LiveMainRepository(
    private val remoteManager: LiveRemoteManager,private val liveMatchDao: LiveMatchDao, private val database: GameDatabase
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    fun observeBalance(): Flow<Long> = database.infoDao().observeBalance()
    fun observeMatchBean(matchId: Long) = liveMatchDao.observeMatchById(matchId)

    // 500-1003: 获取比赛详情
    suspend fun getMatchRes(matchId: Long) {
       var matchFullData = remoteManager.getMatchReq(scope, matchId)?.toRoomData()
        if (matchFullData!=null){
            database.liveMatchDao().insertFullMatch(
                matches = matchFullData.match ,
                markets = matchFullData.markets ,
                selections = matchFullData.selections,
            )
        }
    }
}

