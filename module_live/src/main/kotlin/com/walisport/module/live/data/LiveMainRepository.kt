package com.walisport.module.live.data

import com.walisport.lib_base.data.repository.BaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveMainRepository() : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
}