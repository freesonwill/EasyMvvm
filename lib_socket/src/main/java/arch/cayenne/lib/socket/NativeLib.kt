package arch.cayenne.lib.socket

import android.util.Log
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.socket.data.IRequest
import arch.cayenne.lib.socket.data.IResponse
import arch.cayenne.lib.socket.data.ISecurity
import arch.cayenne.lib.socket.data.InvalidDataResponseError
import arch.cayenne.lib.socket.data.SocketRequestData
import arch.cayenne.lib.socket.data.SocketOriginResponseData
import java.lang.Exception

class NativeLib : ISecurity<IRequest, ByteArray, IResponse> {
    init {
        System.loadLibrary("util")
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

    override fun decrypt(data: ByteArray): IResponse {
        try {
            val unpack = newUnpack(data)
            return if (unpack == null
                || unpack.size != 3
                || unpack[0] !is Int
                || unpack[1] !is Int
                || unpack[2] !is ByteArray) {
                InvalidDataResponseError()
            } else {
                val mid = (unpack[0] as Int).toShort()
                val sid = (unpack[1] as Int).toShort()
                val jsonPayload = unpack[2] as ByteArray
                "message decrypt mid=$mid, sid=$sid proto=${jsonPayload}".logi(NativeLib::class.java.simpleName)
                SocketOriginResponseData(
                    mid = mid,
                    sid = sid,
                    originProto = jsonPayload
                )
            }
        } catch (e: Exception) {
            "message decrypt failed".loge(NativeLib::class.java.simpleName)
            return InvalidDataResponseError()
        }
    }

    override fun encrypt(data: IRequest): ByteArray? {
        if (data !is SocketRequestData) return null
        "encrypt and send data -> mid = ${data.mid}, sid = ${data.sid} data = ${data.payloadByteArray}".logi(
            NativeLib::class.java.simpleName)
        return newPack(
            mid = data.mid,
            sid = data.sid,
            data = data.payloadByteArray ?: byteArrayOf(),
            dataSize = data.payloadByteArray?.size ?: 0
        )
    }

    override fun resetSecurity() {
        reset()
    }
}