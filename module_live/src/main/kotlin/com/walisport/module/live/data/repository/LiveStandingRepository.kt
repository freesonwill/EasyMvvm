package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Sloth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveStandingRepository(
    private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    //获取积分榜实时数据
    suspend fun getCompetitionReq(matchId: Long): Sloth.CompetitionTables? {
        return remoteManager.getCompetitionReq(scope, matchId)
    }
}