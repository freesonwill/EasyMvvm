package com.walisport.lib_socket

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.walisport.lib_socket.data.ConnectClosed
import com.walisport.lib_socket.data.ConnectFailure
import com.walisport.lib_socket.data.ConnectSuccess
import com.walisport.lib_socket.data.IConnectState
import com.walisport.lib_socket.data.IRequest
import com.walisport.lib_socket.data.IResponse
import com.walisport.lib_socket.data.NetworkUnavailable
import com.walisport.lib_socket.data.SocketConnectState
import com.walisport.lib_socket.extension.collectFirstSubscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import okhttp3.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class SocketClientService(
    private val context: WeakReference<Context>
) : ISocket<IRequest, IResponse, IConnectState> {
    private var currentState : SocketConnectState = SocketConnectState.None
    private val workingScope by lazy { CoroutineScope(Dispatchers.IO) }
    private val connectStateFlow : MutableSharedFlow<IConnectState> by lazy {
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

    override suspend fun connect(host: String): SharedFlow<IConnectState> {
        if (currentState != SocketConnectState.None) {
            throw IllegalStateException("socket need to set back to none or using reconnect!")
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
        client.newWebSocket(request, object : WebSocketListener() {
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                currentState = SocketConnectState.Failure
                if (!hasNetworkConnection()) {
                    workingScope.launch { connectStateFlow.emit(NetworkUnavailable()) }
                } else {
                    workingScope.launch { connectStateFlow.emit(ConnectFailure()) }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                currentState = SocketConnectState.Closed
                workingScope.launch { connectStateFlow.emit(ConnectClosed()) }
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                currentState = SocketConnectState.Connecting
                this@SocketClientService.webSocket = webSocket
                workingScope.launch { connectStateFlow.emit(ConnectSuccess()) }
            }
        })

    }

    override fun disConnect() {
        webSocket?.close(1001, null)
    }

    override fun reConnect() {
        if (currentState != SocketConnectState.Connecting) {
            openWebSocket()
        }
    }

    override fun destroy() {
        workingScope.cancel()
        webSocket?.close(1001, null)
        currentState = SocketConnectState.None
    }


    override fun send(data: IRequest) {

    }

    override fun responseObserve(): SharedFlow<IResponse> = socketResponseFlow

    override fun stateChangeObserve(): SharedFlow<IConnectState> = connectStateFlow


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