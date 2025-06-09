package arch.cayenne.lib.websocket.chat

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.websocket.NativeLib
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.IRequest
import arch.cayenne.lib.websocket.data.IResponse
import arch.cayenne.lib.websocket.data.ISecurity
import arch.cayenne.lib.websocket.data.ISocket
import arch.cayenne.lib.websocket.data.InvalidEncryptDataError
import arch.cayenne.lib.websocket.data.InvalidNetworkError
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.lib.websocket.data.SocketOriginResponseData
import arch.cayenne.lib.websocket.data.SocketResponseError
import arch.cayenne.lib.websocket.extension.collectFirstSubscribe
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

class ChatSocketClientService(
    private val context: WeakReference<Application>,
    private val security: ISecurity<IRequest, ByteArray, IResponse>
) : ISocket<IRequest, IResponse, ConnectState> {
    private var currentState: SocketConnectState = SocketConnectState.None
    private val workingScope by lazy { CoroutineScope(Dispatchers.IO) }
    private val connectStateFlow: MutableSharedFlow<ConnectState> by lazy {
        MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 5,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    private val socketResponseFlow: MutableSharedFlow<IResponse> by lazy {
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
        "connect $currentState".logd(this@ChatSocketClientService.javaClass.simpleName)
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
        security.resetSecurity(NativeLib.CIPHER_TYPE_JSON)
        client.newWebSocket(request, object : WebSocketListener() {
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                currentState = SocketConnectState.Failure
                if (!hasNetworkConnection()) {
                    "Socket Client -> NetworkUnavailable".loge(ChatSocketClientService::class.java.simpleName)
                    workingScope.launch { connectStateFlow.emit(ConnectState.NetworkUnavailable) }
                } else {
                    "Socket Client -> ConnectFailure:$t".loge(ChatSocketClientService::class.java.simpleName)
                    workingScope.launch { connectStateFlow.emit(ConnectState.ConnectFailure) }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                "Socket Client -> ConnectClosed".loge(ChatSocketClientService::class.java.simpleName)
                currentState = if (reason == SocketConnectState.None.name) {
                    SocketConnectState.None
                } else {
                    workingScope.launch { connectStateFlow.emit(ConnectState.ConnectClosed) }
                    SocketConnectState.Closed
                }

            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                "Socket Client -> ConnectOpen $currentState".loge(ChatSocketClientService::class.java.simpleName)
                currentState = SocketConnectState.Connecting
                this@ChatSocketClientService.webSocket = webSocket
                workingScope.launch { connectStateFlow.emit(ConnectState.ConnectSuccess) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                "onMessage text $text".logi(this@ChatSocketClientService::class.java.simpleName)
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                try {
                    if (bytes.size != 0) {
                        val byteArray = bytes.toByteArray()
                        val data = security.decrypt(byteArray)
                        if((data as SocketOriginResponseData).originProto?.isNotEmpty() == true)
                        "result ${String((data).originProto ?: byteArrayOf())}".logi(this@ChatSocketClientService::class.java.simpleName)
                        workingScope.launch { socketResponseFlow.emit(data) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    //TODO error
                }
            }
        })

    }

    override fun disConnect(): Boolean {
        val result = webSocket?.close(1001, null) ?: false
        currentState = SocketConnectState.Closed
        return result
    }

    override fun reconnect() {
        if (currentState != SocketConnectState.Connecting && currentState != SocketConnectState.Closed) {
            openWebSocket()
        }
    }

    override fun reset() {
//        "reset webSocket to init state".logi(this::class.java.simpleName)
        webSocket?.close(1001, SocketConnectState.None.name)
    }


    override fun send(data: IRequest): IResponse? {
        if (currentState != SocketConnectState.Connecting) {
            return InvalidNetworkError()
        }
        val byteArray = security.encrypt(data)
        return if (byteArray == null) {
            InvalidEncryptDataError()
        } else {
            webSocket?.send(byteArray.toByteString())
            null
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