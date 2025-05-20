//package arch.cayenne.lib.chatwebsocket
//
//import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
//import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
//import arch.cayenne.lib.chatwebsocket.data.ChatISecurity
//import arch.cayenne.lib.websocket.data.IRequest
//import arch.cayenne.lib.websocket.data.IResponse
//import arch.cayenne.lib.websocket.data.InvalidDataResponseError
//import arch.cayenne.lib.websocket.data.SocketOriginResponseData
//import arch.cayenne.lib.websocket.data.SocketRequestData
//
//class ChatNativeLib : ChatISecurity<IRequest, ByteArray, IResponse> {
//    private val TAG = ChatNativeLib::class.java.simpleName
//    private val CIPHER_TYPE_PB = 1
//    private val CIPHER_TYPE_JSON = 2
//    private var cipherHandler: Long = -1
//
//    companion object {
//        init {
//            System.loadLibrary("chatlib")
//        }
//    }
//
//    init {
//        createNativeCipher()
//    }
//
//    // Native方法
//    external fun createCipher(): Long
//    external fun initCipher(handle: Long, cipherType: Int)
//    external fun destroyCipher(handle: Long)
//    external fun resetCipher(handle: Long)
//    external fun packData(handle: Long, mid: Short, sid: Short, rid: Short, data: ByteArray): ByteArray?
//    external fun unpackData(handle: Long, data: ByteArray): UnpackResult?
//
//    private fun createNativeCipher() {
//        cipherHandler = createCipher()
//        initCipher(cipherHandler, CIPHER_TYPE_JSON)
//    }
//
//    override fun encrypt(data: IRequest): ByteArray? {
//        if (data !is SocketRequestData) return null
//        "Request加密 -> cipherHandler $cipherHandler mid = ${data.mid}, sid = ${data.sid} data = ${data.payloadByteArray?.size}".logi(TAG)
//        return packData(
//            cipherHandler,
//            mid = data.mid,
//            sid = data.sid,
//            rid = data.rid,
//            data = data.payloadByteArray ?: byteArrayOf()
//        )
//    }
//
//    override fun decrypt(data: ByteArray): IResponse {
//        try {
//            val unpack = unpackData(cipherHandler, data) ?: return InvalidDataResponseError()
//            val mid = unpack.mid
//            val sid = unpack.sid
//            val rid = unpack.rid
//            val jsonPayload = unpack.payload
//            "封包解密 mid=$mid, sid=$sid proto=${String(jsonPayload)}".logi(TAG)
//            return SocketOriginResponseData(
//                mid = mid,
//                sid = sid,
//                rid = rid,
//                originProto = jsonPayload
//            )
//        } catch (e: Exception) {
//            "message decrypt failed".loge(TAG)
//            return InvalidDataResponseError()
//        }
//    }
//
//    override fun destroySecurity() {
//        destroyCipher(cipherHandler)
//    }
//
//    override fun resetSecurity() {
//        resetCipher(cipherHandler)
//    }
//
//    data class UnpackResult(
//        val mid: Short,
//        val sid: Short,
//        val rid: Short,
//        val payload: ByteArray
//    ) {
//        override fun equals(other: Any?): Boolean {
//            if (this === other) return true
//            if (other !is UnpackResult) return false
//            return mid == other.mid && sid == other.sid && rid == other.rid &&
//                    payload.contentEquals(other.payload)
//        }
//
//        override fun hashCode(): Int {
//            var result = mid.toInt()
//            result = 31 * result + sid.toInt()
//            result = 31 * result + rid.toInt()
//            result = 31 * result + payload.contentHashCode()
//            return result
//        }
//    }
//
//}