package com.walisport.module.live

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.ChatWebSocketManager
import arch.cayenne.lib.websocket.chat.data.ChatLoginRequestData
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatResponseCode
import arch.cayenne.lib.websocket.chat.extension.chatSendAndWaitProtoMessageResponse
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.chat.data.ChatEnterRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLeaveRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatRoomRequest
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgRequest
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountRequest
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.chat.extension.chatObserveProtoMessage
import arch.cayenne.lib.websocket.data.ApiCode
import arch.cayenne.lib.websocket.data.ConnectState
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform

class LiveRemoteChatManager(
    private val scope: CoroutineScope,
    private val socketManager: ChatWebSocketManager,
    private val userDataManager: UserDataManager
) {
    private val TAG = this.javaClass.simpleName
    private val PLATFORM = 5

    /**
     * 连接聊天服务器
     * */
    suspend fun startSocket(): ConnectState {
        return socketManager.connect("wss://ws.qxe68.com:7001/api/game/chat/ws").first()
    }

    /**
     * 关闭聊天服务器
     * */
    suspend fun disConnect(): Boolean {
        return socketManager.disconnect()
    }

    /**
     * 聊天登陆
     * */
    suspend fun login(): ChatLoginResponseData? {
        val uid = userDataManager.getValue(UserDataKey.KEY_UID, -1)
        val token = userDataManager.getValue(UserDataKey.KEY_TOKEN, "")

//        val uid = 55468987
//        val token = "NTU0Njg5ODdfMTc0NzkwNzU4ODc0MjpnZlRzSGx6MnA5NWt2OVlD"

        val logResp = socketManager.chatSendAndWaitProtoMessageResponse<ChatLoginResponseData>(
            scope, Dispatchers.IO, ApiCode.CHAT_LOGIN, responseCode = ChatResponseCode.LOGIN
        ) {
            ChatLoginRequestData(uid.toLong(), token, PLATFORM)
        }
        if (logResp.error == null && logResp.data != null) {
            return logResp.data
        }
        "login uid:$uid  token:$token   result ${Gson().toJson(logResp)}".logd(TAG)
        return null
    }

    /**
     * 进入聊天室
     * */
    suspend fun enterChatRoom(matchId: Long): ChatEnterRoomResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<ChatEnterRoomResponse>(
            scope,
            Dispatchers.IO,
            ApiCode.CHAT_ENTER_ROOM,
            responseCode = ChatResponseCode.ENTER_CHAT_ROOM_RESP
        ) {
            ChatRoomRequest(matchId, PLATFORM)
        }
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
            scope,
            Dispatchers.IO,
            ApiCode.CHAT_LEAVE_ROOM,
            responseCode = ChatResponseCode.LEAVE_CHAT_ROOM_RESP
        ) {
            ChatRoomRequest(matchId, PLATFORM)
        }
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
            scope,
            Dispatchers.IO,
            ApiCode.CHAT_SEND_MSG,
            responseCode = ChatResponseCode.SEND_MSG_RESP
        ) {
            ChatSendMsgRequest(roomId, content, refUid, refPlatform)
        }
        if (resp.error == null && resp.data != null) {
            return resp.data
        }

        return null
    }

    /**
     * 离开聊天室
     * */
    suspend fun msgNotify(): Flow<MsgNotify> {
        return socketManager.chatObserveProtoMessage<MsgNotify>(ChatResponseCode.MSG_NOTIFY)
            .transform {
                if (it.error != null && it.data != null) {
                    emit(it.data!!)
                }
            }
    }

    /**
     * 校验投注额
     * */
    suspend fun checkBetAmount(): CheckBetAmountResponse? {
        val resp = socketManager.chatSendAndWaitProtoMessageResponse<CheckBetAmountResponse>(
            scope,
            Dispatchers.IO,
            ApiCode.CHAT_CHECK_BETAMOUNT,
            responseCode = ChatResponseCode.CHECK_BET_AMOUNT_RESP
        ) {
            CheckBetAmountRequest(PLATFORM)
        }
        if (resp.error == null && resp.data != null) {
            return resp.data
        }
        return null
    }


}