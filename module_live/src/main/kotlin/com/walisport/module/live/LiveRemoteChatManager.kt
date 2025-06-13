package com.walisport.module.live

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.chat.ChatWebSocketManager
import arch.cayenne.lib.websocket.chat.data.ChatEnterRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLeaveRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLoginRequestData
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatResponseCode
import arch.cayenne.lib.websocket.chat.data.ChatRoomRequest
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgRequest
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountRequest
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountResponse
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryRequest
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.chat.extension.chatObserveProtoMessage
import arch.cayenne.lib.websocket.chat.extension.chatSendAndWaitProtoMessageResponse
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketConnectState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform

class LiveRemoteChatManager(
    private val socketManager: ChatWebSocketManager,
    private val userDataManager: UserDataManager
) {
    private val TAG = this.javaClass.simpleName
    private val PLATFORM = 5

    /**
     * 连接聊天服务器
     * */
    suspend fun startSocket(scope: CoroutineScope): ConnectState? {
        return socketManager.connect(scope, "wss://ws.qxe68.com:7001/api/game/chat/ws")?.first()
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
    suspend fun login(): ChatLoginResponseData? {
        val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
        val token = userDataManager.getValue(UserDataKey.KEY_TOKEN, "")
        
//        val uid = 55469011
//        val token = "NTU0NjkwMTFfMTc0OTI4MDM1NTA0OTpTakJVNGZXSGlOMWx0dTNL" //虚拟机

//        val uid = 55469012
//        val token = "NTU0NjkwMTJfMTc0OTI4MDMzNDY2ODpJeXE5NkJDaUl5OW9XWEVv" //真机

        val logResp = socketManager.chatSendAndWaitProtoMessageResponse<ChatLoginResponseData>(
            ApiCode.CHAT_LOGIN,
            responseCode = ChatResponseCode.LOGIN
        ) {
            ChatLoginRequestData(uid.toLong(), token, PLATFORM)
        }
        if (logResp.error == null && logResp.data != null) {
            return logResp.data
        }
        "login uid:$uid  result ${Gson().toJson(logResp)}".logd(TAG)
        return null
    }

    /**
     * 进入聊天室
     * */
    suspend fun enterChatRoom(matchId: Long): ChatEnterRoomResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<ChatEnterRoomResponse>(
            ApiCode.CHAT_ENTER_ROOM,
            responseCode = ChatResponseCode.ENTER_CHAT_ROOM_RESP,
        ) {
            ChatRoomRequest(matchId, PLATFORM)
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
    suspend fun leaveChatRoom(matchId: Long): ChatLeaveRoomResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<ChatLeaveRoomResponse>(
            ApiCode.CHAT_LEAVE_ROOM,
            responseCode = ChatResponseCode.LEAVE_CHAT_ROOM_RESP,
        ) {
            ChatRoomRequest(matchId, PLATFORM)
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
        refUid: String? = null,
        refPlatform: Int? = null
    ): ChatSendMsgResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<ChatSendMsgResponse>(
            ApiCode.CHAT_SEND_MSG,
            responseCode = ChatResponseCode.SEND_MSG_RESP,
        ) {
            ChatSendMsgRequest(roomId, content, refUid, refPlatform)
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
    suspend fun msgNotify(): Flow<MsgNotify> {
        return socketManager.chatObserveProtoMessage<MsgNotify>(ChatResponseCode.MSG_NOTIFY)
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
            CheckBetAmountRequest(PLATFORM)
        }
        "checkBetAmount  result ${Gson().toJson(resp)}".logd(TAG)

        if (resp.error == null && resp.data != null) {
            return resp.data
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

}