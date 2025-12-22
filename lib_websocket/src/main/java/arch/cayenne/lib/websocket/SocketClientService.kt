package arch.cayenne.lib.websocket

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.IRequest
import arch.cayenne.lib.websocket.data.IResponse
import arch.cayenne.lib.websocket.data.ISecurity
import arch.cayenne.lib.websocket.data.ISocket
import arch.cayenne.lib.websocket.data.InvalidEncryptDataError
import arch.cayenne.lib.websocket.data.InvalidNetworkError
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.lib.websocket.extension.collectFirstSubscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class SocketClientService(
    private val context: WeakReference<Application>,
    private val security: ISecurity<IRequest, ByteArray, IResponse>
) : ISocket<IRequest, IResponse, ConnectState> {
    private val TAG = this::class.java.simpleName
    private var currentState : SocketConnectState = SocketConnectState.None
    private val workingScope by lazy { CoroutineScope(Dispatchers.IO) }
    private val connectStateFlow : MutableSharedFlow<ConnectState> by lazy {
        MutableSharedFlow(
            replay = 1,
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
    private val lock = Any()
    override val socketConnectState: SocketConnectState
        get() = currentState

    /************* Method **************/
    override fun connect(host: String): SharedFlow<ConnectState> {
        //"Socket Client -> connect, host:$host".logi(TAG)
        return when(currentState) {
            SocketConnectState.Connecting -> { connectStateFlow }
            else -> {
                connectStateFlow.collectFirstSubscribe {
                    this.host = host
                    openWebSocket()
                }.shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily)
            }
        }
    }

    private fun openWebSocket() {
        "Socket Client -> openWebSocket".logi(TAG)
        val request = Request.Builder()
            .url(host)
            .build()
        security.resetSecurity()
        client.newWebSocket(request, object : WebSocketListener() {
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                currentState = SocketConnectState.Failure
                if (!hasNetworkConnection()) {
                    "Socket Client -> NetworkUnavailable,${t.message},response:$response".loge(TAG)
                    workingScope.launch { connectStateFlow.emit(ConnectState.NetworkUnavailable) }
                } else {
                    "Socket Client -> ConnectFailure:$t,${t.message},response:$response".loge(TAG)
                    workingScope.launch { connectStateFlow.emit(ConnectState.ConnectFailure) }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                "Socket Client -> ConnectClosed,code:$code,reason:$reason".loge(TAG)
                workingScope.launch { connectStateFlow.emit(ConnectState.ConnectClosed) }
                currentState = if (reason == SocketConnectState.None.name) {
                    SocketConnectState.None
                } else {
                    SocketConnectState.Closed
                }

            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                "Socket Client -> ConnectOpen".logi(TAG)
                currentState = SocketConnectState.Connecting
                this@SocketClientService.webSocket = webSocket
                workingScope.launch { connectStateFlow.emit(ConnectState.ConnectSuccess) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                "onMessage text $text".logi(TAG)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                try {
                    "onMessage bytes $bytes".logi(TAG)
                    if (bytes.size != 0) {
                        val byteArray = bytes.toByteArray()
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

    override fun disConnect():Boolean {
      return  webSocket?.close(1001, null) ?: true
    }

    override fun reconnect() {
        if (currentState != SocketConnectState.None && currentState != SocketConnectState.Connecting && currentState != SocketConnectState.Reconnecting) {
            currentState = SocketConnectState.Reconnecting
            openWebSocket()
        }
    }

    override fun reset() {
        "reset webSocket to init state".logi(TAG)
        //當前狀態不是連線中，不需要特別等socket關掉再設定，直接設定回初始值就好
        if (currentState != SocketConnectState.Connecting) {
            currentState = SocketConnectState.None
            return
        }
        webSocket?.close(1001, SocketConnectState.None.name)
    }


    override fun send(data: IRequest): IResponse? {
        if (currentState != SocketConnectState.Connecting) {
            return InvalidNetworkError()
        }
        return synchronized(lock) {
            val byteArray = security.encrypt(data)
            if (byteArray == null) {
                InvalidEncryptDataError()
            } else {
                webSocket?.send(byteArray.toByteString())
                null
            }
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