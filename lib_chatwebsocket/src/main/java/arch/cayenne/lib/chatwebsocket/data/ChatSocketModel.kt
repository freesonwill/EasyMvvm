package arch.cayenne.lib.chatwebsocket.data

import arch.cayenne.lib.websocket.data.IResponse
import arch.cayenne.lib.websocket.data.ISocketData
import arch.cayenne.lib.websocket.data.SocketResponseError
import com.google.gson.Gson


interface ChatRequestData {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}

data class ChatResponseData<T>(
    override val mid: Short,
    override val sid: Short,
    override val rid: Short,
    val data: T?,
    val error: SocketResponseError? = null,
): ISocketData(), IResponse


data class ChatLoginRequestData(val uid: Long, val token: String, val platform: Int) : ChatRequestData

data class ChatPinRequestData(val data: String) : ChatRequestData

data class ChatLoginResponseData(
    val code: Int? = null,
    val message: String? = null,
    val uid: Long? = null,
    val username: String? = null,
    val avatarId: Int? = null
):IResponse


