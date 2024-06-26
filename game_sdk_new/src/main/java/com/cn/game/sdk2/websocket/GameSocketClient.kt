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
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.ByteBuffer

class GameSocketClient(serverUri: URI?) : WebSocketClient(serverUri) {

    companion object {
        init {
            System.loadLibrary("util")
        }
    }

    init {
        createChiper()
    }

    external fun pack(mid: Short, sid: Short, data: String?, dataSize: Int): ByteArray?
    external fun newPack(mid: Short, sid: Short, data: ByteArray?, dataSize: Int): ByteArray?
    external fun unpack(data: ByteArray?): Array<Any?>?
    external fun newUnpack(data: ByteArray?): Array<Any?>?
    external fun nativeCreateChiper(): Long
    external fun nativeFinalizer(ptr: Long)
    external fun reset()


    private var mNativePtr: Long = 0
    private var _tag = "GameSocketClient"
    private var onMessageListener: OnMessageListener? = null
    var handler:Handler = Handler(Looper.getMainLooper())

    fun setOnMessageListener(listener: OnMessageListener) {
        onMessageListener = listener
    }

    var isReconnecting = false
    fun re() {
        "---尝试重连---".loge()
        isReconnecting = true
        reconnect()
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
//         GlobalScope.launch {
//            withContext(Dispatchers.Main) {
//                reset()
//            }
//        }
        Log.i(_tag, "GameSocketClient-连接成功！")
        handler.post{
            reset()
            if(isReconnecting){
                isReconnecting = false
                if(isLogin) {
                    GameSDK.loginGameWithAgentName(
                        "wali-internal",
                        token,
                        "Gregg Denesik",
                        object : SDKLoginCallbackListener {
                            override fun callback(code: Int, message: String?) {
                                "login:code-${code},message-${message}".loge()
                            }

                        }
                    )
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
        //messageViewModel?.setData(bytes.array())
        handler.post{
            Log.i(_tag, "GameSocketMessage-onMessage")
            if (!bytes!!.hasRemaining()) {
                return@post
            }
            //messageViewModel?.setData(bytes.array())
            val resps = newUnpack(bytes.array())
            val mid = resps!![0] as Int?
            val sid = resps[1] as Int?
            var str = bytes.array()
            if (resps.size > 2) {
                str = (resps[2] as ByteArray?)!!
            }
            Log.i(_tag, "GameSocketMessage-onMessage:mid-$mid sid-$sid")
            onMessageListener?.onMessage(mid, sid, str)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        "socket-onClose-->code:${code}-reason:$reason-remote:$remote".loge(_tag)
        onMessageListener?.onClose(code, reason, remote)
    }

    override fun onError(ex: Exception?) {
        ex?.printStackTrace()
        ex?.message?.loge(_tag)
    }

    private fun createChiper() {
        mNativePtr = 0
        Log.d(_tag, "createChiper1:$mNativePtr")
        mNativePtr = nativeCreateChiper()
        Log.d(_tag, "createChiper2:$mNativePtr")
    }

    protected fun finalize() {
        kotlin.runCatching {
            nativeFinalizer(mNativePtr)
        }
    }


}