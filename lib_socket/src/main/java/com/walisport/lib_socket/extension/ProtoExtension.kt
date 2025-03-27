package com.walisport.lib_socket.extension

import com.google.protobuf.GeneratedMessageLite
import com.walisport.lib_socket.WebSocketManager
import com.walisport.lib_socket.data.IResponse
import com.walisport.lib_socket.data.InvalidProtoTypeResponseError
import com.walisport.lib_socket.data.SocketRequestData
import com.walisport.lib_socket.data.SocketOriginResponseData
import com.walisport.lib_socket.data.SocketResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import java.lang.Exception

fun GeneratedMessageLite<*, *>.asRemoteRequest(mid: Short, sid: Short) : SocketRequestData {
    return SocketRequestData(
        mid = mid,
        sid = sid,
        this.toByteArray()
    )
}

inline fun <reified T: GeneratedMessageLite<*,*>>WebSocketManager.observeProtoMessage(mid: Short, sid: Short) : Flow<IResponse> = getSocketFlow()
    .filterIsInstance<SocketOriginResponseData>()
    .filter {it.mid == mid && it.sid == sid}
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
            return@map InvalidProtoTypeResponseError(mid, sid)
        }
    }
