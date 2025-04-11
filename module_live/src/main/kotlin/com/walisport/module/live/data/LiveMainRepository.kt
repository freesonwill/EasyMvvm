package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveMainRepository() : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
}