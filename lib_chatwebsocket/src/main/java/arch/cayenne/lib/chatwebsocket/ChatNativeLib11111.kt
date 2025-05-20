//package arch.cayenne.lib.chatwebsocket
//
//
//import android.util.Log
//import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
//import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
//import arch.cayenne.lib.chatwebsocket.data.ChatISecurity
//import arch.cayenne.lib.websocket.data.IRequest
//import arch.cayenne.lib.websocket.data.IResponse
//import arch.cayenne.lib.websocket.data.InvalidDataResponseError
//import arch.cayenne.lib.websocket.data.SocketOriginResponseData
//import arch.cayenne.lib.websocket.data.SocketRequestData
//
//class ChatNativeLib11111 : ChatISecurity<IRequest, ByteArray, IResponse> {
//    companion object {
//        const val CIPHER_TYPE_PB = 1
//        const val CIPHER_TYPE_JSON = 2
//    }
//
//    init {
//        System.loadLibrary("chatlib")
//        createChiper()
//    }
//
//    external fun init(cipherType: Int)
//    external fun pack(mid: Short, sid: Short, rid: Short, data: String?, dataSize: Int): ByteArray?
//    external fun newPack(mid: Short, sid: Short, rid: Short, data: ByteArray?, dataSize: Int): ByteArray?
//    external fun unpack(data: ByteArray?): Array<Any?>?
//    external fun newUnpack(data: ByteArray?): Array<Any?>?
//    external fun nativeCreateChiper(): Long
//    external fun nativeFinalizer(ptr: Long)
//    external fun reset()
//
//    private var mNativePtr: Long = 0
//
//    private fun createChiper() {
//        mNativePtr = 0
//        Log.d("ChatNativeLib", "createChiper1:$mNativePtr")
//        mNativePtr = nativeCreateChiper()
//        Log.d("ChatNativeLib", "createChiper2:$mNativePtr")
//        init(CIPHER_TYPE_JSON) // Default to JSON cipher type
//    }
//
//    protected fun finalize() {
//        Log.d("ChatNativeLib", "finalize:$mNativePtr")
//        kotlin.runCatching {
//            nativeFinalizer(mNativePtr)
//        }
//    }
//
//    override fun decrypt(data: ByteArray): IResponse {
//        try {
//            val unpack = newUnpack(data)
//            "封包解密 ${unpack?.size}  ${unpack?.toList()}  \n ${unpack?.get(0) !is Int} ${unpack?.get(1) !is Int}  ${unpack?.get(2) !is Int} ${unpack?.get(3) !is ByteArray}".logi(ChatNativeLib11111::class.java.simpleName)
//
//            return if (unpack == null
//                || unpack.size != 4
//                || unpack[0] !is Int
//                || unpack[1] !is Int
//                || unpack[2] !is Int
//                || unpack[3] !is ByteArray) {
//                InvalidDataResponseError()
//            } else {
//                val mid = (unpack[0] as Int).toShort()
//                val sid = (unpack[1] as Int).toShort()
//                val rid = (unpack[2] as Int).toShort()
//                val jsonPayload = unpack[3] as ByteArray
//                "封包解密1 mid=$mid, sid=$sid size ${jsonPayload.size} proto=${jsonPayload}".logi(ChatNativeLib11111::class.java.simpleName)
//                SocketOriginResponseData(
//                    mid = mid,
//                    sid = sid,
//                    rid = rid,
//                    originProto = jsonPayload
//                )
//            }
//        } catch (e: Exception) {
//            "message decrypt failed".loge(ChatNativeLib11111::class.java.simpleName)
//            return InvalidDataResponseError()
//        }
//    }
//
//    override fun encrypt(data: IRequest): ByteArray? {
//        if (data !is SocketRequestData) return null
//        "Request加密 -> mid = ${data.mid}, sid = ${data.sid} data = ${data.payloadByteArray}".logi(
//            ChatNativeLib11111::class.java.simpleName)
//        return newPack(
//            mid = data.mid,
//            sid = data.sid,
//            rid = data.rid,
//            data = data.payloadByteArray ?: byteArrayOf(),
//            dataSize = data.payloadByteArray?.size ?: 0
//        )
//    }
//
//    override fun destroySecurity() {
//
//    }
//
//    override fun resetSecurity() {
//        reset()
//    }
//}