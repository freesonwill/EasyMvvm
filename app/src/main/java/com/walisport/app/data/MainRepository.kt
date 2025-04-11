package com.walisport.app.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.socket.WebSocketManager
import kotlinx.coroutines.CoroutineScope

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:58
 * @description:
 */
class MainRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager
) : BaseRepository() {

}