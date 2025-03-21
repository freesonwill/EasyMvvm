package com.walisport.app.data

import com.walisport.lib_base.data.remote.Response
import com.walisport.lib_base.data.repository.BaseRepository
import com.walisport.lib_base.utils.LogUtilsExt.logi
import com.walisport.lib_common.websocket.WebSocketManager
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:58
 * @description:
 */
class MainRepository(private val socketManager: WebSocketManager) : BaseRepository() {

    suspend fun startSocket() {
        socketManager.connect("wss://ws.qxe68.com:7001/api/game/52002").collect {
            "$it".logi(this.javaClass.simpleName)
        }
    }

    // 此為範例
    suspend fun login(): Response {
        return scope.async {
            // TODO 處理登入
            delay(1_000L)
            Response.Success
        }.await()
    }
}