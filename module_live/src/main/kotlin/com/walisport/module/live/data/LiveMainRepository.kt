package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.MatchBean
import com.walisport.module.live.LiveRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveMainRepository(
    private val remoteManager: LiveRemoteManager, private val database: GameDatabase
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val matchDao = database.matchDao()

    suspend fun getMatchBean(matchId: Long): MatchBean {
        return matchDao.getMatchById(matchId)

    }

}

