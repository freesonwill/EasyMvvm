package arch.cayenne.lib.socket

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.socket.data.ConnectState
import arch.cayenne.lib.socket.data.IRequest
import arch.cayenne.lib.socket.data.IResponse
import arch.cayenne.lib.socket.data.ISecurity
import arch.cayenne.lib.socket.data.ISocket
import arch.cayenne.lib.socket.data.SocketConnectState
import arch.cayenne.lib.socket.extension.collectFirstSubscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class SocketClientService(
    private val context: WeakReference<Application>,
    private val security: ISecurity<IRequest, ByteArray, IResponse>
) : ISocket<IRequest, IResponse, ConnectState> {
    private var currentState : SocketConnectState = SocketConnectState.None
    private val workingScope by lazy { CoroutineScope(Dispatchers.IO) }
    private val connectStateFlow : MutableSharedFlow<ConnectState> by lazy {
        MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 5,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    private val socketResponseFlow : MutableSharedFlow<IResponse> by lazy {
        MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 10,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    private var webSocket: WebSocket? = null
    private var host: String = ""

    override suspend fun connect(host: String): SharedFlow<ConnectState> {
        if (currentState != SocketConnectState.None && currentState != SocketConnectState.Closed) {
            throw IllegalStateException("socket need to set back to none or using reconnect! but now state is $currentState")
        }

        return connectStateFlow.collectFirstSubscribe {
            this.host = host
            openWebSocket()
        }.shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily)
    }

    private fun openWebSocket() {
        val request = Request.Builder()
            .url(host)
            .build()
        security.resetSecurity()
        client.newWebSocket(request, object : WebSocketListener() {
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                currentState = SocketConnectState.Failure
                if (!hasNetworkConnection()) {
                    "Socket Client -> NetworkUnavailable".loge(SocketClientService::class.java.simpleName)
                    workingScope.launch { connectStateFlow.emit(ConnectState.NetworkUnavailable) }
                } else {
                    "Socket Client -> ConnectFailure:$t".loge(SocketClientService::class.java.simpleName)
                    workingScope.launch { connectStateFlow.emit(ConnectState.ConnectFailure) }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                "Socket Client -> ConnectClosed".loge(SocketClientService::class.java.simpleName)
                currentState = if (reason == SocketConnectState.None.name) {
                    SocketConnectState.None
                } else {
                    workingScope.launch { connectStateFlow.emit(ConnectState.ConnectClosed) }
                    SocketConnectState.Closed
                }

            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                "Socket Client -> ConnectOpen".loge(SocketClientService::class.java.simpleName)
                currentState = SocketConnectState.Connecting
                this@SocketClientService.webSocket = webSocket
                workingScope.launch { connectStateFlow.emit(ConnectState.ConnectSuccess) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                "onMessage text $text".logi(this@SocketClientService::class.java.simpleName)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                try {
                    "onMessage bytes $bytes".logi(this@SocketClientService::class.java.simpleName)
                    if (bytes.size != 0) {
                        val byteArray = bytes.toByteArray()
                        workingScope.launch(Dispatchers.Main) {

                        }
                        val data = security.decrypt(byteArray)
                        workingScope.launch { socketResponseFlow.emit(data) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    //TODO error
                }
            }
        })

    }

    override fun disConnect() {
        webSocket?.close(1001, null)
    }

    override fun reconnect() {
        if (currentState != SocketConnectState.Connecting) {
            openWebSocket()
        }
    }

    override fun reset() {
        "reset webSocket to init state".logi(this::class.java.simpleName)
        webSocket?.close(1001, SocketConnectState.None.name)
    }


    override fun send(data: IRequest) {
        val byteArray = security.encrypt(data)
        if (byteArray != null) {
            webSocket?.send(byteArray.toByteString())
        }

    }

    override fun responseObserve(): SharedFlow<IResponse> = socketResponseFlow

    override fun stateChangeObserve(): SharedFlow<ConnectState> = connectStateFlow


    private fun hasNetworkConnection(): Boolean {
        val connectivityManager =
            context.get()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkAvailability =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkAvailability != null
                && networkAvailability.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && networkAvailability.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

}