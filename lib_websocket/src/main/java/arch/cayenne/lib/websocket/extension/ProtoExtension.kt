package arch.cayenne.lib.websocket.extension

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.WebSocketManager.Companion.responseTimeout
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.InvalidProtoTypeResponseError
import arch.cayenne.lib.websocket.data.ResponseTimeOutError
import arch.cayenne.lib.websocket.data.SimpleResponseError
import arch.cayenne.lib.websocket.data.SocketOriginResponseData
import arch.cayenne.lib.websocket.data.SocketRequestData
import arch.cayenne.lib.websocket.data.SocketResponseData
import arch.cayenne.lib.websocket.data.SocketResponseError
import com.google.protobuf.GeneratedMessageLite
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

fun GeneratedMessageLite<*, *>.asRemoteRequest(apiCode: ApiCode, rid: Short) : SocketRequestData {
    return SocketRequestData(
        mid = apiCode.mid,
        sid = apiCode.sid,
        rid = rid,
        this.toByteArray()
    )
}

inline fun <reified T: GeneratedMessageLite<*,*>> WebSocketManager.observeProtoMessage(apiCode: ApiCode) : Flow<SocketResponseData<T>> = getSocketFlow()
    .filterIsInstance<SocketOriginResponseData>()
    .filter { it.mid == apiCode.mid && it.sid == apiCode.sid }
    .map {
        try {
            val TAG = WebSocketManager::class.java.simpleName
            val proto = it.originProto?.let { byteArray ->
                T::class.java.getMethod("parseFrom", ByteArray::class.java)
                    .invoke(null, byteArray) as T
            }
            val success:Boolean = runCatching { T::class.java.getMethod("getSuccess").invoke(proto) as Boolean }
                .getOrNull() ?: true
            val error:String = runCatching { T::class.java.getMethod("getMessage").invoke(proto) as String }
                .getOrNull() ?: ""
            if(success) {
                "sendAndWaitProtoMessageResponse map proto apiCode:$apiCode,rid:${it.rid},success".logi(TAG)
            } else {
                "sendAndWaitProtoMessageResponse map proto apiCode:$apiCode,rid:${it.rid},error:$error".loge(TAG)
            }
            return@map SocketResponseData(
                mid = it.mid,
                sid = it.sid,
                rid = it.rid,
                data = if(success) proto else null,
                error = if(!success) SimpleResponseError(error) else null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@map SocketResponseData(
                mid = it.mid,
                sid = it.sid,
                rid = it.rid,
                data = null,
                error = InvalidProtoTypeResponseError()
            )
        }
    }

suspend inline fun<reified T: GeneratedMessageLite<*,*>> WebSocketManager.sendAndWaitProtoMessageResponse(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    apiCode: ApiCode,
    timeout: Long = responseTimeout,
    crossinline request: () -> GeneratedMessageLite<*, *>
): SocketResponseData<T> = withContext(Dispatchers.IO){
    val apiStart = System.currentTimeMillis()
    val rid = nextRid()
    val errorRes = send(request.invoke().asRemoteRequest(apiCode, rid))
    return@withContext if (errorRes != null && errorRes is SocketResponseError) {
        "api:$apiCode rid:$rid overall execution time is: ${System.currentTimeMillis() - apiStart}ms".logi(WebSocketManager::class.java.name)
        SocketResponseData(
            mid = apiCode.mid,
            sid = apiCode.sid,
            rid = rid,
            data = null,
            error = errorRes
        )
    } else {
        val responseData = withTimeoutOrNull(timeout) {
            observeProtoMessage<T>(apiCode).filter { it.rid == rid }.first()
        }
        "api:$apiCode rid:$rid overall execution time is: ${System.currentTimeMillis() - apiStart}ms".logi(WebSocketManager::class.java.name)
        responseData ?: SocketResponseData(
            mid = apiCode.mid,
            sid = apiCode.sid,
            rid = rid,
            data = null,
            error = ResponseTimeOutError()
        )
    }
}
