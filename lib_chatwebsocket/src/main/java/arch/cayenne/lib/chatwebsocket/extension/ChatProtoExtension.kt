package arch.cayenne.lib.chatwebsocket.extension

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.chatwebsocket.ChatSocketClientService
import arch.cayenne.lib.chatwebsocket.ChatWebSocketManager
import arch.cayenne.lib.chatwebsocket.ChatWebSocketManager.Companion.responseTimeout
import arch.cayenne.lib.chatwebsocket.data.ChatDataToJson
import arch.cayenne.lib.chatwebsocket.data.ChatResponseData
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.IResponse
import arch.cayenne.lib.websocket.data.InvalidProtoTypeResponseError
import arch.cayenne.lib.websocket.data.ResponseTimeOutError
import arch.cayenne.lib.websocket.data.SocketOriginResponseData
import arch.cayenne.lib.websocket.data.SocketRequestData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull

fun ChatDataToJson.chatAsRemoteRequest(
    apiCode: ApiCode,
    rid: Short
): SocketRequestData {
    val json = toJson()
    "json $json".logd(ChatSocketClientService::class.java.simpleName)
    return SocketRequestData(
        mid = apiCode.mid,
        sid = apiCode.sid,
        rid = rid,
        json.toByteArray()
    )
}

inline fun <reified T : IResponse> ChatWebSocketManager.chatObserveProtoMessage(
    apiCode: ApiCode
): Flow<ChatResponseData<T>> = getSocketFlow()
    .filterIsInstance<SocketOriginResponseData>()
    .map {
        "chat map".logi(ChatWebSocketManager::class.java.simpleName)
        try {
            val proto = it.originProto?.let { byteArray ->
//                val json = String(byteArray)
                Gson().fromJson(String(byteArray),T::class.java)
            }
            "string to json bean success sid -> ${it.sid}".logi(ChatWebSocketManager::class.java.simpleName)
            return@map ChatResponseData(
                mid = it.mid,
                sid = it.sid,
                rid = it.rid,
                data = proto,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@map ChatResponseData(
                mid = it.mid,
                sid = it.sid,
                rid = it.rid,
                data = null,
                error = InvalidProtoTypeResponseError()
            )
        }
    }

suspend inline fun <reified T : IResponse> ChatWebSocketManager.chatSendAndWaitProtoMessageResponse(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    apiCode: ApiCode,
    rid: Short = 0,
    timeout: Long? = null,
    request: () -> ChatDataToJson
): ChatResponseData<T> {
    val deferred = scope.async(dispatcher) {
        withTimeoutOrNull(timeout ?: responseTimeout) {
            chatObserveProtoMessage<T>(apiCode).filter {
                it.rid == rid
            }.first()
        }
    }
    send(request.invoke().chatAsRemoteRequest(apiCode, rid))
    return deferred.await() ?: ChatResponseData(
        mid = apiCode.mid,
        sid = apiCode.sid,
        rid = rid,
        data = null,
        error = ResponseTimeOutError()
    )
}
