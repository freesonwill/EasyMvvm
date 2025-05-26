package com.walisport.module.topup.data

import com.walisport.module.topup.TopUpRemoteManager
import arch.cayenne.lib.base.data.repository.BaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class TopUpMainRepository(
    private val remoteManager: TopUpRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

}

