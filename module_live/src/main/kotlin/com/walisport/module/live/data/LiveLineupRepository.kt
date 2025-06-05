package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Sloth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveLineupRepository(  private val remoteManager: LiveRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ////获取阵容实时数据
    suspend fun getMatchLiveReq( matchId: Long ): Sloth.MatchLineupDetail? {
        return remoteManager.getMatchLiveReq(scope, matchId)
    }
}