package arch.cayenne.lib.websocket.chat.extension

import android.annotation.SuppressLint
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.websocket.chat.ChatSocketClientService
import arch.cayenne.lib.websocket.chat.ChatWebSocketManager
import arch.cayenne.lib.websocket.chat.ChatWebSocketManager.Companion.responseTimeout
import arch.cayenne.lib.websocket.chat.data.ChatRequestData
import arch.cayenne.lib.websocket.chat.data.ChatResponseCode
import arch.cayenne.lib.websocket.chat.data.ChatResponseData
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.IResponse
import arch.cayenne.lib.websocket.data.InvalidProtoTypeResponseError
import arch.cayenne.lib.websocket.data.ResponseTimeOutError
import arch.cayenne.lib.websocket.data.SocketOriginResponseData
import arch.cayenne.lib.websocket.data.SocketRequestData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.CoroutineContext

@SuppressLint("SuspiciousIndentation")
fun ChatRequestData.chatAsRemoteRequest(
    apiCode: ApiCode,
    rid: Short
): SocketRequestData {
    val json = toJson()
    if(apiCode != ApiCode.CHAT_PING)
    "chat request json  $json".logd(ChatSocketClientService::class.java.simpleName)
    return SocketRequestData(
        mid = apiCode.mid,
        sid = apiCode.sid,
        rid = rid,
        json.toByteArray()
    )
}

inline fun <reified T : IResponse> ChatWebSocketManager.chatObserveProtoMessage(
    responseCode: ChatResponseCode
): Flow<ChatResponseData<T>> = getSocketFlow()
    .filterIsInstance<SocketOriginResponseData>()
    .filter {
//        "it mid ${it.mid} ${responseCode.mid}  sid ${it.sid} ${responseCode.sid}  ${String(it.originProto ?: byteArrayOf())}".logd(ChatWebSocketManager::class.java.simpleName)
        it.mid == responseCode.mid && it.sid == responseCode.sid }
    .map {
//        "chat map".logi(ChatWebSocketManager::class.java.simpleName)
        try {
            val bean = it.originProto?.let { byteArray ->
                Gson().fromJson(String(byteArray), T::class.java)
            }
            "string to json bean success sid -> ${it.sid}".logi(ChatWebSocketManager::class.java.simpleName)
            return@map ChatResponseData(
                mid = it.mid,
                sid = it.sid,
                rid = it.rid,
                data = bean,
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
    apiCode: ApiCode,
    responseCode: ChatResponseCode,
    rid: Short = nextRid(),
    timeout: Long = responseTimeout,
    crossinline request: () -> ChatRequestData
): ChatResponseData<T> {
    val response = withContext(Dispatchers.IO) {
        send(request.invoke().chatAsRemoteRequest(apiCode, rid))
        withTimeoutOrNull(timeout) {
            chatObserveProtoMessage<T>(responseCode).filter {
                it.rid == rid
            }.first()
        }
    }
    return response ?: ChatResponseData(
        mid = apiCode.mid,
        sid = apiCode.sid,
        rid = rid,
        data = null,
        error = ResponseTimeOutError()
    )
}
