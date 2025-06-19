package arch.cayenne.lib.websocket.data

import arch.cayenne.lib.base.data.remote.ApiFailedState
import com.google.protobuf.GeneratedMessageLite

interface IRequest

interface IResponse

//Socket有收到message後處理出錯
interface SocketResponseError: ApiFailedState {
    override val msg: String
}

abstract class ISocketData {
    abstract val mid: Short
    abstract val sid: Short
    abstract val rid: Short
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
    override val rid: Short,
    val payloadByteArray: ByteArray?
): ISocketData(), IRequest

@Suppress("ArrayInDataClass")
data class SocketOriginResponseData(
    override val mid: Short,
    override val sid: Short,
    override val rid: Short,
    val originProto: ByteArray?
): ISocketData(), IResponse

data class SocketResponseData<T: GeneratedMessageLite<*,*>>(
    override val mid: Short,
    override val sid: Short,
    override val rid: Short,
    val data: T?,
    val error: SocketResponseError? = null,
): ISocketData(), IResponse

//在解密過程錯誤
data class InvalidDataResponseError(
    override val msg: String = "Invalid socket data type!",
    override val code: Int? = null
): IResponse, SocketResponseError

//解析的proto的類型錯誤，檢查是否給錯proto type
data class InvalidProtoTypeResponseError(
    override val msg: String = "Invalid proto type or missing proto mapping!",
    override val code: Int? = null
) : SocketResponseError, ApiFailedState

//等待API回來時超出預期時間
data class ResponseTimeOutError(
    override val msg: String = "response time out!!",
    override val code: Int? = null
) : SocketResponseError, ApiFailedState

//沒有token
data class LoginTokenFailedError(
    override val msg: String = "no token or uid data!!",
    override val code: Int? = null
) : SocketResponseError, ApiFailedState

data class InvalidNetworkError(
    override val msg: String = "Network is not available, please check your connection!",
    override val code: Int? = null
) : IResponse, SocketResponseError, ApiFailedState

data class InvalidEncryptDataError(
    override val msg: String = "Invalid encrypted data, decryption failed!",
    override val code: Int? = null
) : IResponse, SocketResponseError, ApiFailedState
