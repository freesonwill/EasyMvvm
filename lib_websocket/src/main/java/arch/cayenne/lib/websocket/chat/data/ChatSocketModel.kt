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
    abstract val code:Int
}



/**
 * 聊天登陆
 * */
data class ChatLoginRequestData(val uid: Long, val token: String, val platform: Int) :
    ChatRequestData

data class ChatLoginResponseData(
    override val code: Int,
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

/**
 * @param code 0成功 1失败
 * */
data class ChatEnterRoomResponse( override val code: Int,val chatroomId:Long):IResponse,ChatResponseBase()

data class ChatLeaveRoomResponse( override val code: Int):IResponse,ChatResponseBase()

/**
 * 发送消息
 * */
data class ChatSendMsgRequest(val roomId: Long,val content:String,val refUid:String? = null,val refPlatform:Int? = null):ChatRequestData

data class ChatSendMsgResponse(override val code: Int,val errorMessage:String? = ""):IResponse,ChatResponseBase()

/**
 * 获取聊天记录
 * */
data class GetChatHistoryRequest(val roomId: Long,val page:Int,val pageSize:Int,val requestId:String?):ChatRequestData

data class GetChatHistoryResponse(override val code: Int,val msgs:List<ChatMsg>,val totalPages:Int,val totalRecords:Int,val requestId:String?):IResponse,ChatResponseBase()


data class MsgNotify(val roomId:Long,val msg:ChatMsg):IResponse

/**
 * 消息Bean
 * */
data class ChatMsg(val uid:String,val userName:String,val avatarId:Int,val msgId:String,val content:String,val timestamp:String,val refUid:String,val refUserName:String,
                   val refAvatarId:Int,val onlyForSelf:Int,val platform:Int)

/**
 * 获取用户统计消息
 * */
data class GetUserStatisticRequest(val uid:String,val platform:Int):ChatRequestData

data class GetUserStatisticResponse(override val code:Int,val roundCount:Long,val validBetScore:Long):IResponse,ChatResponseBase()

/**
 * 设置用户头像
 * */
data class ChatSetUserAvatarRequest(val avatarId:Int,val platform:Int):ChatRequestData

data class ChatSetUserAvatarResponse(override val code: Int):IResponse,ChatResponseBase()

/**
 * 更新用户信息
 * */
data class ChatSyncUserNameRequest(val platform:Int):ChatRequestData

data class ChatSyncUserNameResponse(override val code: Int):IResponse,ChatResponseBase()

/**
 * 校验投注额
 * */
data class CheckBetAmountRequest(val platform:Int):ChatRequestData

data class CheckBetAmountResponse(override val code: Int,val validBetScore:Long,val errorMessage:String):IResponse,ChatResponseBase()