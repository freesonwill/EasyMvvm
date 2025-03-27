package com.walisport.app.data

import com.walisport.lib_base.data.repository.BaseRepository
import com.walisport.lib_base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.IConnectState
import com.walisport.lib_socket.data.SocketResponseData
import com.walisport.lib_socket.data.SocketResponseError
import com.walisport.lib_socket.extension.asRemoteRequest
import com.walisport.lib_socket.extension.observeProtoMessage
import galaxy.client.proto.Client
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:58
 * @description:
 */
class MainRepository(private val socketManager: WebSocketManager) : BaseRepository() {

}