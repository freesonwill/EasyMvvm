package arch.cayenne.module.chat

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.CHAT_SERVER
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.chat.ChatWebSocketManager
import arch.cayenne.lib.websocket.chat.data.ChatEnterRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLeaveRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLoginRequestData
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.lib.websocket.chat.data.ChatResponseCode
import arch.cayenne.lib.websocket.chat.data.ChatRoomRequest
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgRequest
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountRequest
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountResponse
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryRequest
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.chat.data.MsgType
import arch.cayenne.lib.websocket.chat.data.ReportUserRequest
import arch.cayenne.lib.websocket.chat.data.ReportUserResponse
import arch.cayenne.lib.websocket.chat.extension.chatObserveMessage
import arch.cayenne.lib.websocket.chat.extension.chatSendAndWaitProtoMessageResponse
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketConnectState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform

class RemoteChatManager(
    private val socketManager: ChatWebSocketManager,
    private val userDataManager: UserDataManager
) {
    private val TAG = this.javaClass.simpleName
//    private val PLATFORM = 5

    /**
     * 连接聊天服务器
     * */
    suspend fun startSocket(scope: CoroutineScope): ConnectState? {
        return socketManager.connect(scope, CHAT_SERVER)?.first()

    }

    /**
     * 关闭聊天服务器
     * */
    suspend fun disConnect(scope: CoroutineScope): Boolean {
        return socketManager.disconnect(scope)
    }

    /**
     * 聊天登陆
     * */
    suspend fun login(chatType: ChatType): ChatLoginResponseData? {
        val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
        val token = userDataManager.getValue(UserDataKey.KEY_TOKEN, "")

//        val uid = 55469174
//        val token = "NTU0NjkxNzRfMTc1MzYwNzAxOTAwMjpYWUQ0Tm12Vjc1YTlDTnFi"

        val logResp = socketManager.chatSendAndWaitProtoMessageResponse<ChatLoginResponseData>(
            ApiCode.CHAT_LOGIN,
            responseCode = ChatResponseCode.LOGIN
        ) {
            ChatLoginRequestData(uid.toLong(), token, chatType.value)
        }
        "login uid:$uid  result ${Gson().toJson(logResp)}".logd(TAG)
        if (logResp.error == null && logResp.data != null) {
            socketManager.setLoginFlow(logResp.data!!)
            return logResp.data
        }
        return null
    }

    /**
     * 进入聊天室
     * */
    suspend fun enterChatRoom(matchId: Long, chatType: ChatType): ChatEnterRoomResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<ChatEnterRoomResponse>(
            ApiCode.CHAT_ENTER_ROOM,
            responseCode = ChatResponseCode.ENTER_CHAT_ROOM_RESP,
        ) {
            ChatRoomRequest(matchId, chatType.value)
        }
        "enterChatRoom matchId:$matchId   result ${Gson().toJson(resp)}".logd(TAG)

        if (resp.error == null && resp.data != null) {
            return resp.data
        }
        return null
    }

    /**
     * 离开聊天室
     * */
    suspend fun leaveChatRoom(matchId: Long, chatType: ChatType): ChatLeaveRoomResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<ChatLeaveRoomResponse>(
            ApiCode.CHAT_LEAVE_ROOM,
            responseCode = ChatResponseCode.LEAVE_CHAT_ROOM_RESP,
        ) {
            ChatRoomRequest(matchId, chatType.value)
        }
        "leaveChatRoom matchId:$matchId   result ${Gson().toJson(resp)}".logd(TAG)

        if (resp.error == null && resp.data != null) {
            return resp.data
        }
        return null
    }

    /**
     * 发送消息
     * */
    suspend fun sendMsgNotify(
        roomId: Long,
        content: String,
        chatType: ChatType,
        msgType: MsgType,
        extraData: Map<String,String>? = null,
        refUid: List<String>?,
    ): ChatSendMsgResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<ChatSendMsgResponse>(
            ApiCode.CHAT_SEND_MSG,
            responseCode = ChatResponseCode.SEND_MSG_RESP,
        ) {
            ChatSendMsgRequest(roomId, content, refUid, chatType.value, msgType.value, extraData)
        }
        "sendMsgNotify matchId:$roomId   result ${Gson().toJson(resp)}".logd(TAG)

        if (resp.error == null && resp.data != null) {
            return resp.data
        }
        return null
    }

    /**
     * 监听消息
     * */
    @OptIn(FlowPreview::class)
    suspend fun msgNotify(): Flow<MsgNotify> {
        return socketManager.chatObserveMessage<MsgNotify>()
            .buffer(100)
            .debounce(60) //60ms内只处理最后一条消息
            .transform {
                if (it.error == null && it.data != null) {
                    "msgNotify  result ${Gson().toJson(it.data)}".logd(TAG)
                    emit(it.data!!)
                }
            }
    }

    /**
     * 校验投注额
     * */
    suspend fun checkBetAmount(): CheckBetAmountResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<CheckBetAmountResponse>(
            ApiCode.CHAT_CHECK_BETAMOUNT,
            responseCode = ChatResponseCode.CHECK_BET_AMOUNT_RESP,
        ) {
            CheckBetAmountRequest()
        }
        "checkBetAmount  result ${Gson().toJson(resp)}".logd(TAG)

        if (resp.error == null && resp.data != null) {
            return resp.data
        }
        return null
    }

    suspend fun reportUser(uid:String,chatType: ChatType,type:Int):ReportUserResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<ReportUserResponse>(
            ApiCode.CHAT_REPORT_USER,
            responseCode = ChatResponseCode.REPORT_USER_RESP,
        ) {
            ReportUserRequest(uid,chatType.value,type)
        }
      "reportOther uid:$uid   result ${Gson().toJson(resp)}".logd(TAG)
        if(resp.error == null && resp.data != null){
            return resp.data//do nothing
        }
        return null
    }


    suspend fun getChatHistory(
        roomId: Long,
        page: Int,
        pageSize: Int,
        requestId: String
    ): GetChatHistoryResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<GetChatHistoryResponse>(
            ApiCode.CHAT_HISTORY,
            responseCode = ChatResponseCode.CHAT_HISTORY
        ) {
            GetChatHistoryRequest(roomId, page, pageSize, requestId)
        }
        "getChatHistory matchId:$roomId  result ${resp.data?.msgs?.size}".logd(TAG)

        if (resp.error == null && resp.data != null) {
            return resp.data
        }
        return null
    }

    fun getConnectStateFlow(): StateFlow<SocketConnectState> =
        socketManager.getSocketConnectStateFlow()

    fun getLoginFlow(): StateFlow<ChatLoginResponseData?> {
        return socketManager.getLoginFlow()
    }

}