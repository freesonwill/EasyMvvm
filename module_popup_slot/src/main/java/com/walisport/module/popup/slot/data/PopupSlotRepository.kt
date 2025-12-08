package com.walisport.module.popup.slot.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope

class PopupSlotRepository(
    override val scope: CoroutineScope ,
    private val socketManager: WebSocketManager ,
    private val manager: UserDataManager
) : BaseRepository() {

    var anchorX: Float = UNINITIALIZED_ANCHOR
    var anchorY: Float = UNINITIALIZED_ANCHOR

    companion object {
        const val UNINITIALIZED_ANCHOR = -1f
    }
}