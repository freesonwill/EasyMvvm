package com.walisport.lib_socket.data

interface IRequest

interface IResponse

//Socket有收到message後處理出錯
interface SocketError {
    val msg: String
}

abstract class ISocketData {
    abstract val mid: Short
    abstract val sid: Short
}

interface IConnectState
//socket連線成功
class ConnectSuccess : IConnectState
//socket關閉，通常是主動關閉
class ConnectClosed : IConnectState
//socket錯誤，網路斷聯
class NetworkUnavailable : IConnectState
//socket錯誤
class ConnectFailure : IConnectState



@Suppress("ArrayInDataClass")
data class SocketRequestData(
    override val mid: Short,
    override val sid: Short,
    val payloadByteArray: ByteArray?
): ISocketData(), IRequest

@Suppress("ArrayInDataClass")
data class SocketResponseData(
    override val mid: Short,
    override val sid: Short,
    val originProto: ByteArray?
): ISocketData(), IResponse

//在解密過程錯誤
data class InvalidDataError(
    override val msg: String = "Invalid socket data type!"
): IResponse, SocketError


