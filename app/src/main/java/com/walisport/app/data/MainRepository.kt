package com.walisport.app.data

import com.walisport.lib_base.data.repository.BaseRepository
import com.walisport.lib_socket.WebSocketManager
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