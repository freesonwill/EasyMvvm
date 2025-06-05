package arch.cayenne.lib.websocket.chat.data

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
    val code: Int? = null,
    val data: T?,
    val error: SocketResponseError? = null,
): ISocketData(), IResponse

abstract class ChatResponseBase{
    abstract val code:Int?
}

/**
 * 聊天登陆
 * */
data class ChatLoginRequestData(val uid: Long, val token: String, val platform: Int) :
    ChatRequestData

data class ChatLoginResponseData(
    override val code: Int?,
    val message: String? = null,
    val uid: Long? = null,
    val username: String? = null,
    val avatarId: Int? = null
):IResponse,ChatResponseBase()
/**
 * 聊天心跳
 * */
data class ChatPinRequestData(val data: String) : ChatRequestData

/**
 * 聊天进入或离开聊天室
 */
data class ChatRoomRequest(val roomId:Long,val platform:Int):ChatRequestData

data class ChatEnterRoomResponse( override val code: Int?,val chatroomId:Long):IResponse,ChatResponseBase()

data class ChatLeaveRoomResponse( override val code: Int?):IResponse,ChatResponseBase()

/**
 * 发送消息
 * */
data class ChatSendMsgRequest():ChatRequestData