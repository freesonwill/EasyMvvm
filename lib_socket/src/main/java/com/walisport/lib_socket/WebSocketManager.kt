package com.walisport.lib_socket

import com.walisport.lib_base.utils.LogUtilsExt.loge
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class WebSocketManager(
   private val socket : ISocket<IRequest, IResponse, ConnectState>
) {
    private val workingScope by lazy { CoroutineScope(Dispatchers.IO) }

    private var heartbeatJob: Job? = null
    private var heartbeatDispatcher: ExecutorCoroutineDispatcher? = null

    private var reconnectJob: Job? = null
    private var reconnectDispatcher: ExecutorCoroutineDispatcher? = null

    private var retryCount = 0

    companion object {
        private const val heartbeatInterval: Long = 10000
        private const val reconnectInterval: Long = 5000
    }
    init {
        observeState()
    }

    suspend fun connect(host: String) : Flow<ConnectState> {
        return socket.connect(host)
    }

    private fun observeState(){
        workingScope.launch {
            getConnectStateFlow().collect { state ->
                when(state) {
                    is ConnectState.ConnectSuccess -> {
                        stopReconnect()
                        startHeartbeat()
                    }
                    else -> {
                        stopHeartbeat()
                        startReconnect()
                    }
                }
            }
        }

    }

    fun disconnect() {
        socket.disConnect()
    }

    private fun reconnect() {
        socket.reconnect()
    }

    fun destroy() {
        socket.destroy()
        stopHeartbeat()
    }

    fun send(data: IRequest) {
        socket.send(data)
    }

    private fun startReconnect() {
        "startReconnect!".logi(this.javaClass.simpleName)
        reconnectJob?.cancel()
        reconnectDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        reconnectJob = CoroutineScope(reconnectDispatcher!!).launch {
            while (true) {
                delay(reconnectInterval)
                retryCount++
                "try to reconnect! retry count = $retryCount".logi(this.javaClass.simpleName)
                reconnect()
            }
        }
    }
    private fun stopReconnect() {
        "stop reconnect!".logi(this.javaClass.simpleName)
        retryCount = 0
        reconnectJob?.cancel()
        reconnectDispatcher?.close()
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
    private fun stopHeartbeat() {
        "stopHeartbeat!".logi(this.javaClass.simpleName)
        heartbeatJob?.cancel()
        heartbeatDispatcher?.close()
    }

    fun getSocketFlow(): Flow<IResponse> = socket.responseObserve()
    fun getConnectStateFlow(): Flow<ConnectState> = socket.stateChangeObserve()
}