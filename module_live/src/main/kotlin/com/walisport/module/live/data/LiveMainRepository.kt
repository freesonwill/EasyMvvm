package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.live.LiveRemoteManager
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveMainRepository( private val remoteManager: LiveRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    suspend fun getMatchReq( matchId: Long ):Common.Match?  {
        return remoteManager.getMatchReq(scope, matchId)?.firstOrNull{it.matchId==matchId}
    }

}