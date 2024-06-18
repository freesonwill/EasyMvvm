package com.cn.game.sdk2.websocket

import android.util.Log
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


    private var mNativePtr: Long = 0
    private var _tag = "GameSocketClient"
    private var onMessageListener: OnMessageListener? = null

    fun setOnMessageListener(listener: OnMessageListener) {
        onMessageListener = listener
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i(_tag, "GameSocketClient-连接成功！")
    }

    override fun onMessage(message: String?) {
        Log.i(_tag, "GameSocketMessage-$message")
    }

    override fun onMessage(bytes: ByteBuffer?) {
        if (!bytes!!.hasRemaining()) {
            return
        }
        val resps = newUnpack(bytes.array())
        val mid = resps!![0] as Int?
        val sid = resps[1] as Int?
        var str = bytes.array()
        if (resps.size > 2) {
            str = (resps[2] as ByteArray?)!!
        }
        onMessageListener?.onMessage(mid, sid, str)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i(_tag, "GameSocketClose-code:$code")
        Log.i(_tag, "GameSocketClose-reason:$reason")
        Log.i(_tag, "GameSocketClose-remote:$remote")
    }

    override fun onError(ex: Exception?) {
        ex?.printStackTrace()
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