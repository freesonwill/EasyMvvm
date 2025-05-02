package com.walisport.module.message.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.message.MessageRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MessageMainRepository(
    private val remoteManager: MessageRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

}

