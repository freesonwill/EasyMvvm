package com.walisport.lib_socket.data

import com.google.protobuf.GeneratedMessageLite

interface IRequest

interface IResponse

//Socket有收到message後處理出錯
interface SocketResponseError {
    val msg: String
}

abstract class ISocketData {
    abstract val mid: Short
    abstract val sid: Short
}

sealed class ConnectState {
    data object ConnectSuccess : ConnectState()
    data object ConnectClosed : ConnectState()
    data object NetworkUnavailable : ConnectState()
    data object ConnectFailure : ConnectState()
}

@Suppress("ArrayInDataClass")
data class SocketRequestData(
    override val mid: Short,
    override val sid: Short,
    val payloadByteArray: ByteArray?
): ISocketData(), IRequest

@Suppress("ArrayInDataClass")
data class SocketOriginResponseData(
    override val mid: Short,
    override val sid: Short,
    val originProto: ByteArray?
): ISocketData(), IResponse

data class SocketResponseData<T: GeneratedMessageLite<*,*>>(
    override val mid: Short,
    override val sid: Short,
    val responseData: T?
): ISocketData(), IResponse

//在解密過程錯誤
data class InvalidDataResponseError(
    override val msg: String = "Invalid socket data type!"
): IResponse, SocketResponseError

//解析的proto的類型錯誤，檢查是否給錯proto type
data class InvalidProtoTypeResponseError(
    val mid: Short,
    val sid: Short,
    override val msg: String = "mid = $mid, sid = $sid, Invalid proto type or missing proto mapping!"
) : IResponse, SocketResponseError

data class ResponseTimeOutError(
    override val msg: String = "response time out!!"
) : IResponse, SocketResponseError


