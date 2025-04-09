package com.walisport.module.home.repository

import com.google.protobuf.GeneratedMessageLite
import com.walisport.lib.base.data.repository.BaseRepository
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.ApiCode
import com.walisport.lib_socket.data.SocketResponseData
import com.walisport.lib_socket.extension.observeProtoMessage
import com.walisport.lib_socket.extension.sendAndWaitProtoMessageResponse
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeRepository(
    override val scope: CoroutineScope,
    val socketManager: WebSocketManager,
) : BaseRepository() {
    suspend inline fun <reified T: GeneratedMessageLite<*, *>>getStatistical(): SocketResponseData<T> {
        return socketManager.sendAndWaitProtoMessageResponse<T>(
            scope = scope,
            dispatcher = Dispatchers.IO,
            apiCode = ApiCode.STATISTICAL,
        ) {
            Client.StatisticalReq.newBuilder().build()
        }
    }
}