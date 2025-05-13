package arch.cayenne.lib.websocket.extension

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.lib.websocket.WebSocketManager.Companion.responseTimeout
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.InvalidProtoTypeResponseError
import arch.cayenne.lib.websocket.data.ResponseTimeOutError
import arch.cayenne.lib.websocket.data.SocketOriginResponseData
import arch.cayenne.lib.websocket.data.SocketRequestData
import arch.cayenne.lib.websocket.data.SocketResponseData
import com.google.protobuf.GeneratedMessageLite
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

            val proto = it.originProto?.let { byteArray ->
                T::class.java.getMethod("parseFrom", ByteArray::class.java)
                    .invoke(null, byteArray) as T
            }
            "observeProtoMessage map proto success sid -> ${it.sid}".logi(WebSocketManager::class.java.simpleName)
            return@map SocketResponseData(
                mid = it.mid,
                sid = it.sid,
                rid = it.rid,
                data = proto,
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
    rid: Short = 0,
    timeout: Long? = null,
    request: () -> GeneratedMessageLite<*, *>
): SocketResponseData<T> {
    val deferred = scope.async(dispatcher) {
        withTimeoutOrNull(timeout ?: responseTimeout) {
            observeProtoMessage<T>(apiCode).filter { it.rid == rid }.first()
        }
    }
    send(request.invoke().asRemoteRequest(apiCode, rid))
    return deferred.await() ?: SocketResponseData(
        mid = apiCode.mid,
        sid = apiCode.sid,
        rid = rid,
        data = null,
        error = ResponseTimeOutError()
    )
}
