package com.walisport.module.setting.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope

class OddsDisplayRepository(
    override val scope: CoroutineScope,
    private val manager: UserDataManager,
    private val socketManager: WebSocketManager
) : BaseRepository() {

}