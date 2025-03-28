package com.walisport.lib_socket

import com.walisport.lib_base.utils.LogUtilsExt.logi
import com.walisport.lib_socket.data.ApiCode
import com.walisport.lib_socket.data.ConnectState
import com.walisport.lib_socket.data.IRequest
import com.walisport.lib_socket.data.IResponse
import com.walisport.lib_socket.data.ISocket
import com.walisport.lib_socket.extension.asRemoteRequest
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class WebSocketManager(
   private val socket : ISocket<IRequest, IResponse, ConnectState>
) {
    private var connectStateFlow: Flow<ConnectState>? = null
    private val workingScope by lazy { CoroutineScope(Dispatchers.IO) }

    private var heartbeatJob: Job? = null
    private var heartbeatDispatcher: ExecutorCoroutineDispatcher? = null

    companion object {
        private const val heartbeatInterval: Long = 10000
    }

    suspend fun connect(host: String) : Flow<ConnectState> {
        return socket.connect(host).also {
            connectStateFlow = it
        }.map { state ->
            when(state) {
                is ConnectState.ConnectSuccess -> {
                    startHeartbeat()
                }
                else -> {
                    stopHeartbeat()
                }
            }
            state
        }
    }

    fun disconnect() {
        socket.disConnect()
    }

    fun reConnect() {
        socket.reConnect()
    }

    fun destroy() {
        socket.destroy()
        stopHeartbeat()
    }

    private fun startHeartbeat() {
        "startHeartbeat!".logi(this.javaClass.simpleName)
        heartbeatJob?.cancel()
        heartbeatDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        heartbeatJob = CoroutineScope(heartbeatDispatcher!!).launch {
            while (true) {
                delay(heartbeatInterval)
                "Send Heartbeat!".logi(this.javaClass.simpleName)

                socket.send(
                    Client.PingBackReq.newBuilder().apply {
                        this.data = "1234567"
                    }.build().asRemoteRequest(ApiCode.PING)
                )
            }
        }
    }

    fun send(data: IRequest) {
        socket.send(data)
    }

    private fun stopHeartbeat() {
        "stopHeartbeat!".logi(this.javaClass.simpleName)
        heartbeatJob?.cancel()
        heartbeatDispatcher?.close()
    }

    fun getSocketFlow(): Flow<IResponse> = socket.responseObserve()
}