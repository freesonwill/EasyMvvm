package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Sloth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveOutsRepository(
    private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //获取比赛趋势实时数据
    suspend fun getMatchTrendReq(matchId: Long): Sloth.MatchTrendData? {
        return remoteManager.getMatchTrendReq(scope, matchId)
    }
}