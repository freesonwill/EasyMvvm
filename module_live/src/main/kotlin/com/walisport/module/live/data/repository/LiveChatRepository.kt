package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.data.ConnectState
import com.walisport.module.live.LiveRemoteChatManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class LiveChatRepository(val remote: LiveRemoteChatManager) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun startSocket(): ConnectState {
        return remote.startSocket()
    }

    suspend fun disconnect(): Boolean {
        return remote.disConnect()
    }

    suspend fun login(): ChatLoginResponseData? {
        return remote.login(scope)
    }
}