package com.cn.game.sdk2.websocket

import android.util.Log
import com.cn.game.sdk2.utils.ext.CommonExt.isMainThread
import com.cn.game.sdk2.websocket.imp.GameApp
import com.xcjh.base_lib2.utils.loge
import com.xcjh.base_lib2.utils.logi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer
import java.util.Timer
import java.util.TimerTask

internal class GameSocketClient(serverUri: URI?) : WebSocketClient(serverUri) {

    private var _tag = "SocketClient"
    private var onMessageListener: OnMessageListener? = null
    private val reconnectInterval: Long = 1000
    private var timer: Timer? = null

    fun setOnMessageListener(listener: OnMessageListener) {
        onMessageListener = listener
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onOpen(handshakedata: ServerHandshake?) {
        "GameSocketClient-连接成功！onOpen,isMainThread:${isMainThread}".logi(_tag)
        gameAboutModel.isOpen = true
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                isTokenValid = true
                if (isLogin) {
                    "重连成功，需要重新登录，登录Token：${gameAboutModel.token}".logi(_tag)
                    GameApp.login(
                        gameAboutModel.token, gameAboutModel.agentName, gameAboutModel.isAnchor
                    )
                }
            }
        }
        timer?.cancel()
        timer = null
        startHeartbeat()
        appListener?.runOnUiThread { initSuccessful() }
    }

    override fun onMessage(message: String?) {
        Log.i(_tag, "GameSocketMessage-$message")
    }

    override fun onMessage(bytes: ByteBuffer?) {
        if (!bytes!!.hasRemaining()) {
            return
        }
        val resp: Array<Any?>? = nativeLib.newUnpack(bytes.array())
        resp?.let {
            val mid = it[0] as Int?
            val sid = it[1] as Int?
            var str = bytes.array()
            if (it.size > 2) {
                str = (it[2] as ByteArray?)!!
            }
            "GameSocketMessage-onMessage:mid-$mid sid-$sid".logi(_tag)
            onMessageListener?.onMessage(mid, sid, str)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        "socket-onClose-->code:${code}-reason:$reason-remote:$remote,isMainThread:${isMainThread}".loge(
            _tag
        )
        gameAboutModel.isOpen = false
        nativeLib.reset()
        reconnectHandle()
        stopHeartbeat()
        onMessageListener?.onClose(code, reason, remote)
    }


    override fun onError(ex: Exception?) {
        "onError:${ex?.message}".loge(_tag)
        ex?.printStackTrace()
        if (ex is WebsocketNotConnectedException) reconnectHandle()
    }

    private fun reconnectHandle() {
        timer?.cancel()
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    "GameSDK start reconnect".loge(_tag)
                    if (!isOpen && isNeedReconnect) reconnect()
                }
            }, reconnectInterval)
        }
    }

    // 心跳实现
    private var heartbeatTask: TimerTask? = null
    private val heartbeatInterval: Long = 10000 //

    private fun startHeartbeat() {
        heartbeatTask = object : TimerTask() {
            // 发送心跳消息
            override fun run() {
                "startHeartbeat".logi(_tag)
                gameMassageManager?.ping()
            }
        }
        Timer().schedule(heartbeatTask, heartbeatInterval, heartbeatInterval)
    }

    private fun stopHeartbeat() {
        heartbeatTask?.cancel()
        heartbeatTask = null
    }
}