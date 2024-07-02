package com.cn.game.sdk2.websocket

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.cn.game.sdk2.websocket.imp.GameSDK
import com.cn.game.sdk2.websocket.interfaces.SDKLoginCallbackListener
import com.xcjh.base_lib.utils.loge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

class GameSocketClient(serverUri: URI?) : WebSocketClient(serverUri) {

    private var _tag = "GameSocketClient"
    private var onMessageListener: OnMessageListener? = null

    fun setOnMessageListener(listener: OnMessageListener) {
        onMessageListener = listener
    }

    fun re() {
        "---尝试重连---".loge()
        if (isNeedReconnect) {
            nativeLib.reset()
            reconnect()
        }
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i(_tag, "GameSocketClient-连接成功！")
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                isTokenValid = true
                if (isLogin) {
                    GameSDK.loginGameWithAgentName(
                        "wali-internal",
                        token,
                        "Gregg Denesik",
                        object : SDKLoginCallbackListener {
                            override fun callback(code: Int, message: String?) {
                                "login:code-${code},message-${message}".loge()
                            }

                        })
                }
            }
        }
    }

    override fun onMessage(message: String?) {
        Log.i(_tag, "GameSocketMessage-$message")
    }

    override fun onMessage(bytes: ByteBuffer?) {
        if (!bytes!!.hasRemaining()) {
            return
        }
        Log.i(_tag, "GameSocketMessage-onMessage")
        val resp: Array<Any?>? = nativeLib.newUnpack(bytes.array())
        resp?.let {
            val mid = it[0] as Int?
            val sid = it[1] as Int?
            var str = bytes.array()
            if (it.size > 2) {
                str = (it[2] as ByteArray?)!!
            }
            Log.i(_tag, "GameSocketMessage-onMessage:mid-$mid sid-$sid")
            onMessageListener?.onMessage(mid, sid, str)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        "socket-onClose-->code:${code}-reason:$reason-remote:$remote".loge(_tag)
        nativeLib.reset()
        onMessageListener?.onClose(code, reason, remote)
    }

    override fun onError(ex: Exception?) {
        ex?.printStackTrace()
        ex?.message?.loge(_tag)
    }
}