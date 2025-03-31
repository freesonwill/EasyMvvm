package com.walisport.lib_socket.extension

import com.google.protobuf.GeneratedMessageLite
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.WebSocketManager.Companion.responseTimeout
import com.walisport.lib_socket.data.ApiCode
import com.walisport.lib_socket.data.IResponse
import com.walisport.lib_socket.data.InvalidProtoTypeResponseError
import com.walisport.lib_socket.data.ResponseTimeOutError
import com.walisport.lib_socket.data.SocketRequestData
import com.walisport.lib_socket.data.SocketOriginResponseData
import com.walisport.lib_socket.data.SocketResponseData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.lang.Exception

fun GeneratedMessageLite<*, *>.asRemoteRequest(apiCode: ApiCode) : SocketRequestData {
    return SocketRequestData(
        mid = apiCode.mid,
        sid = apiCode.sid,
        this.toByteArray()
    )
}

inline fun <reified T: GeneratedMessageLite<*,*>>WebSocketManager.observeProtoMessage(apiCode: ApiCode) : Flow<IResponse> = getSocketFlow()
    .filterIsInstance<SocketOriginResponseData>()
    .filter {it.mid == apiCode.mid && it.sid == apiCode.sid}
    .map {
        try {
            val proto = it.originProto?.let { byteArray ->
                T::class.java.getMethod("parseFrom", ByteArray::class.java)
                    .invoke(null, byteArray) as T
            }
            return@map SocketResponseData(
                mid = it.mid,
                sid = it.sid,
                responseData = proto
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@map InvalidProtoTypeResponseError(apiCode.mid, apiCode.sid)
        }
    }

suspend inline fun<reified T: GeneratedMessageLite<*,*>>WebSocketManager.sendAndWaitProtoMessageResponse(
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    apiCode: ApiCode,
    timeout: Long? = null,
    request: () -> GeneratedMessageLite<*, *>
): IResponse {
    val deferred = scope.async(dispatcher) {
        withTimeoutOrNull(timeout ?: responseTimeout) {
            observeProtoMessage<T>(apiCode).first()
        }
    }
    send(request.invoke().asRemoteRequest(apiCode))
    return deferred.await() ?: ResponseTimeOutError()
}
