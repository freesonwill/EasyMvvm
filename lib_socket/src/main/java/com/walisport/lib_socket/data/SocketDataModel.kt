package com.walisport.lib_socket.data

interface IRequest

interface IResponse

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


