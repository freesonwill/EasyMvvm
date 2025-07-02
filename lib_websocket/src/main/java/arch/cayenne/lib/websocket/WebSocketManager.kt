package arch.cayenne.lib.websocket

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.IRequest
import arch.cayenne.lib.websocket.data.IResponse
import arch.cayenne.lib.websocket.data.ISocket
import arch.cayenne.lib.websocket.data.ThreadSafeAutoIncrementID
import arch.cayenne.lib.websocket.extension.asRemoteRequest
import galaxy.client.proto.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class WebSocketManager(
    private val socket : ISocket<IRequest, IResponse, ConnectState>,
    private val connectionManager: ConnectivityManager
) {
    private val workingScope by lazy { CoroutineScope(Dispatchers.IO) }

    private var heartbeatJob: Job? = null
    private var heartbeatDispatcher: ExecutorCoroutineDispatcher? = null

    private var reconnectJob: Job? = null
    private var reconnectDispatcher: ExecutorCoroutineDispatcher? = null

    private var retryCount = 0
    //线程安全的自增Rid
    private val ridGenerator by lazy { ThreadSafeAutoIncrementID(max = 0xFFF) } //4095
    fun nextRid() = ridGenerator.id.toShort()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    companion object {
        private const val heartbeatInterval: Long = 10000
        private const val reconnectInterval: Long = 2000
        private const val maxRetryCount: Int = 3

        const val responseTimeout: Long = 5000
    }
    init {
        observeState()
    }

    fun connect(host: String) : Flow<ConnectState> {
//        setNetWorkCallback()
        return socket.connect(host)
    }

    /***
     * 監聽網路如果重新連線，即時的去做一次重連動作，可以避免等待重連
     * */
    private fun setNetWorkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    "network is available, reconnect immediately!".logi(this@WebSocketManager::class.java.simpleName)
                    reconnect()
                }
            }
        }
        connectionManager.registerNetworkCallback(request, networkCallback!!)
    }

    private fun observeState(){
        workingScope.launch {
            getConnectStateFlow().collect { state ->
                    when(state) {
                        is ConnectState.ConnectSuccess -> {
                            retryCount = 0
                            stopReconnect()
                            startHeartbeat()
                        }
                        is ConnectState.ConnectClosed -> Unit
                        is ConnectState.ReconnectFailure -> {
                            stopReconnect()
                        }
                        else -> {
                            stopHeartbeat()
                        }
                    }
            }
        }

    }

    fun disconnect() {
        stopHeartbeat()
        socket.disConnect()
    }

     fun reconnect() {
        socket.reconnect()
    }

    fun reset() {
        socket.reset()
        networkCallback?.apply { connectionManager.unregisterNetworkCallback(this) }
        stopHeartbeat()
    }

    fun send(data: IRequest): IResponse? {
        return socket.send(data)
    }

    fun startReconnect() {
        if (reconnectJob?.isActive == true) return
        "startReconnect!".logi(this.javaClass.simpleName)
        reconnectJob?.cancel()
        reconnectDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        reconnectJob = CoroutineScope(reconnectDispatcher!!).launch {
            while (retryCount < maxRetryCount) {
                delay(reconnectInterval)
                retryCount++
                "try to reconnect! retry count = $retryCount".logi(this.javaClass.simpleName)
                reconnect()
            }
        }
    }
    private fun stopReconnect() {
        "stop reconnect!".logi(this.javaClass.simpleName)
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
                    }.build().asRemoteRequest(ApiCode.PING, 0)
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
    fun getConnectStateFlow(): Flow<ConnectState> = socket.stateChangeObserve().transform {
        if ((it is ConnectState.ConnectFailure || it is ConnectState.NetworkUnavailable) && retryCount >= maxRetryCount) {
            emit(ConnectState.ReconnectFailure)
        } else {
            emit(it)
        }
    }
}