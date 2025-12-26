package arch.cayenne.lib.websocket.chat.data

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.data.IResponse
import arch.cayenne.lib.websocket.data.ISocketData
import arch.cayenne.lib.websocket.data.SocketResponseError
import com.google.gson.Gson
import game.chat.proto.GameChat
import game.chat.proto.GameChatCommon.RefUser


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
) : ISocketData(), IResponse

abstract class ChatResponseBase {
    abstract val code: Int
}

//  LOBBY = 1; // 大厅
//    GAME = 2;  // 游戏
enum class ChatType(val value: Int) {
    LOBBY(1),
    GAME(2);

    companion object {
        fun getChatType(value: Int): ChatType? {
            return when (value) {
                1 -> LOBBY
                2 -> GAME
                else -> null
            }
        }
    }
}

enum class MsgType(val value: Int) {
    MSG_TYPE_SYSTEM(0),
    MSG_TYPE_TEXT(1),
    MSG_TYPE_SHARE(2);

    companion object {
        fun getMsgType(value: Int): MsgType? {
            return when (value) {
                0 -> MSG_TYPE_SYSTEM
                1 -> MSG_TYPE_TEXT
                2 -> MSG_TYPE_TEXT
                else -> null
            }
        }
    }
}

// 通知类型
enum class NotificationType(val value: Int) {
    NOTIFICATION_TYPE_UNSPECIFIED(0),
    USER_BANNED(1),          // 用户封禁
    MERCHANT_BANNED(2),     // 商户封禁
    USERNAME_SENSITIVE(3),   // 用户名涉及敏感词
    USERNAME_NORMAL(4),      // 用户名正常
    USER_REPORT(5),          // 用户被举报
    USER_BANNED_NOTIFY(6),   // 封禁用户通知
}

/**
 * 聊天登陆
 * */
data class ChatLoginRequestData(
    val uid: Long,
    val token: String,
    val chatType: Int
) :
    ChatRequestData

data class ChatLoginResponseData(
    override val code: Int,
    val message: String? = null,
    val uid: Long? = null,
    val username: String? = null,
    val avatarId: Int? = null
) : IResponse, ChatResponseBase()

/**
 * 聊天心跳
 * */
data class ChatPinRequestData(val data: String) : ChatRequestData

/**
 * 聊天进入或离开聊天室
 */
data class ChatRoomRequest(val roomId: Long, val chatType: Int) : ChatRequestData

/**
 * @param code 0成功 1失败
 * */
data class ChatEnterRoomResponse(override val code: Int, val chatroomId: Long) : IResponse,
    ChatResponseBase()

data class ChatLeaveRoomResponse(override val code: Int) : IResponse, ChatResponseBase()

/**
 * 发送消息
 * */
data class ChatSendMsgRequest(
    val roomId: Long,
    val content: String,
    val refUid: List<Long>? = null,
    val chatType: ChatType,
    val msgType: MsgType,
    val extraData: Map<String, String>? = null
) : ChatRequestData

data class ChatSendMsgResponse(override val code: Int, val errorMessage: String? = "") : IResponse,
    ChatResponseBase()

/**
 * 获取聊天记录
 * */
data class GetChatHistoryRequest(
    val roomId: Long,
    val page: Int,
    val pageSize: Int,
    val requestId: String?
) : ChatRequestData

data class GetChatHistoryResponse(
    override val code: Int,
    val msgs: List<ChatMsg>,
    val totalPages: Int,
    val totalRecords: Int,
    val requestId: String?
) : IResponse, ChatResponseBase()


data class MsgNotify(val roomId: Long, val msg: ChatMsg) : IResponse

data class ChatRefUser(
    val uid: Long,
    val userName: String,
    val avatarId: Int,
    val replaceRefUserName: String? = null
)

/**
 * 消息Bean
 * */
data class ChatMsg(
    val uid: String,
    val userName: String,
    val avatarId: Int,
    val msgId: String,
    val content: String,
    val timestamp: String,
    val refUid: List<Long>? = null,
    val refInfos: Map<Long, ChatRefUser>? = null,
    val onlyForSelf: Int,
    val replaceUserName: String? = null,
    val msgType: MsgType,
    val extraData: Map<String, String>? = null,
    val chatType: ChatType
) {
}
//{"avatarId":1,"content":"官方推荐大家多多交流！","msgId":"1766242779790","onlyForSelf":0,"platform":0,"refAvatarId":0,"timestamp":"2025-12-20T14:59:39.790Z","uid":"18446744073709551615","userName":"我是官方"}
/**
 * 获取用户统计消息
 * */
data class GetUserStatisticRequest(val uid: String) : ChatRequestData

data class GetUserStatisticResponse(
    override val code: Int,
    val roundCount: Long,
    val validBetScore: Long
) : IResponse, ChatResponseBase()

/**
 * 设置用户头像
 * */
data class ChatSetUserAvatarRequest(val avatarId: Int) : ChatRequestData

data class ChatSetUserAvatarResponse(override val code: Int) : IResponse, ChatResponseBase()

/**
 * 更新用户信息
 * */
class ChatSyncUserNameRequest() : ChatRequestData

data class ChatSyncUserNameResponse(override val code: Int) : IResponse, ChatResponseBase()

/**
 * 校验投注额
 * */
class CheckBetAmountRequest() : ChatRequestData

data class CheckBetAmountResponse(
    override val code: Int,
    val score: Long,
    val errorMessage: String
) : IResponse, ChatResponseBase()

/**
 * 举报请求
 * */
data class ReportUserRequest(
    val uid: Long,
    val chatType: Int,
    val type: String
) : ChatRequestData

data class ReportUserResponse(override val code: Int) : IResponse, ChatResponseBase()

data class SyncNotificationRequest(
    val type: Int,
    val payload: Map<String, String>
) : ChatRequestData
