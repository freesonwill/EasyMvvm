package arch.cayenne.lib.websocket.chat

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatPinRequestData
import arch.cayenne.lib.websocket.chat.extension.chatAsRemoteRequest
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.IRequest
import arch.cayenne.lib.websocket.data.IResponse
import arch.cayenne.lib.websocket.data.ISocketManager
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.lib.websocket.data.ThreadSafeAutoIncrementID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.Executors

class ChatWebSocketManager(
    private val socket: ChatSocketClientService
): ISocketManager {
    private val TAG = this::class.java.simpleName
    private val workingScope by lazy { CoroutineScope(Dispatchers.IO) }
    private var heartbeatJob: Job? = null
    private var heartbeatDispatcher: ExecutorCoroutineDispatcher? = null

    private var reconnectJob: Job? = null
    private var reconnectDispatcher: ExecutorCoroutineDispatcher? = null

    private var retryCount = 0

    private var _loginFlow:MutableStateFlow<ChatLoginResponseData?> = MutableStateFlow(null)

    //线程安全的自增Rid
    private val ridGenerator by lazy { ThreadSafeAutoIncrementID(max = 0xFFF) } //4095
    fun nextRid() = ridGenerator.id.toShort()

//    //关闭服务Deffer
//    var disconnectDeffer: Deferred<Boolean>? = null

    companion object {
        private const val heartbeatInterval: Long = 10000
        private const val reconnectInterval: Long = 5000
        const val responseTimeout: Long = 5000
    }

    init {
        "${socket}".logi("webchat")
        observeState()
    }

    suspend fun connect(scope: CoroutineScope,host: String): Flow<ConnectState>? {
        val deferred =  scope.async(Dispatchers.IO) {
            withTimeoutOrNull(responseTimeout){
                socket.connect(host)
            }
        }
        return deferred.await()
    }

    private fun observeState() {
        workingScope.launch {
            getConnectStateFlow().collect { state ->
                when (state) {
                    is ConnectState.ConnectSuccess -> {
                        stopReconnect()
                        startHeartbeat()
                    }

                    is ConnectState.ConnectClosed -> {
                        stopReconnect()
                        stopHeartbeat()
                    }

                    else -> {
                        stopHeartbeat()
                        startReconnect()
                    }
                }
            }
        }

    }

    /**
     * 2s之内重新调用connect 取消disconnect
     * */
    suspend fun disconnect(scope: CoroutineScope): Boolean {
//        disconnectDeffer = scope.async(Dispatchers.IO) {
//            delay(2000)
//            socket.disConnect()
//        }
//        return disconnectDeffer?.await() ?: true
        val deferred = scope.async(Dispatchers.IO) {
            withTimeoutOrNull(responseTimeout){
                socket.disConnect()
            }
        }
        return deferred.await() ?: false
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
                "try to reconnect! retry count = $retryCount".logi(TAG)
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
                "Send Heartbeat!".logi(TAG)
                socket.send(
                    ChatPinRequestData("1234567").chatAsRemoteRequest(ApiCode.CHAT_PING, 0)
                )
            }
        }
    }

    private fun stopHeartbeat() {
        "stopHeartbeat!".logi(this.javaClass.simpleName)
        heartbeatJob?.cancel()
        heartbeatDispatcher?.close()
    }

    fun setLoginFlow(loginData:ChatLoginResponseData) {
        _loginFlow.tryEmit(loginData)
    }

    override fun getSocketFlow(): Flow<IResponse> = socket.responseObserve()
    override fun getConnectStateFlow(): Flow<ConnectState> = socket.stateChangeObserve()
    override val socketConnectState: SocketConnectState get() = socket.socketConnectState
    fun getSocketConnectStateFlow(): StateFlow<SocketConnectState> = socket.socketConnectStateFlow()
    fun getMessageFlow():Flow<IResponse> = socket.messageFlow()
    fun getLoginFlow():StateFlow<ChatLoginResponseData?> = _loginFlow


}