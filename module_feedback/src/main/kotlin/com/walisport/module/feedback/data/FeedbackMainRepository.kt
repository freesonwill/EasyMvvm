package com.walisport.module.feedback.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import com.walisport.module.feedback.FeedbackRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class FeedbackMainRepository(
    private val remoteManager: FeedbackRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

}

