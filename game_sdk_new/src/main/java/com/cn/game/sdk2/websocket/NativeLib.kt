package com.cn.game.sdk2.websocket

import android.util.Log

class NativeLib {

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
    private fun createChiper() {
        mNativePtr = 0
        Log.d("NativeLib", "createChiper1:$mNativePtr")
        mNativePtr = nativeCreateChiper()
        Log.d("NativeLib", "createChiper2:$mNativePtr")
    }

    protected fun finalize() {
        Log.d("NativeLib", "finalize:$mNativePtr")
        kotlin.runCatching {
            nativeFinalizer(mNativePtr)
        }
    }
}