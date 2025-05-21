package arch.cayenne.lib.chatwebsocket

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.chatwebsocket.data.ChatISocket
import arch.cayenne.lib.chatwebsocket.data.ChatPinRequestData
import arch.cayenne.lib.chatwebsocket.extension.chatAsRemoteRequest
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.IRequest
import arch.cayenne.lib.websocket.data.IResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class ChatWebSocketManager(
    private val socket : ChatISocket<IRequest, IResponse, ConnectState>
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
        const val responseTimeout: Long = 5000
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
//                        startHeartbeat()
                    }
                    is ConnectState.ConnectClosed -> Unit
                    else -> {
                        stopHeartbeat()
                        startReconnect()
                    }
                }
            }
        }

    }

    fun disconnect():Boolean {
       return socket.disConnect()
    }

    private fun reconnect() {
        socket.reconnect()
    }

    fun reset() {
        socket.reset()
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
                   ChatPinRequestData("1234567").chatAsRemoteRequest(ApiCode.PING, 0)
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