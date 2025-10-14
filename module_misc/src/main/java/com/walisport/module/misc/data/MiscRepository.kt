package com.walisport.module.misc.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope

class MiscRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val manager: UserDataManager
) : BaseRepository() {


}