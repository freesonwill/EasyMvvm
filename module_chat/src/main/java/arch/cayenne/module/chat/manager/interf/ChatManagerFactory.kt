package arch.cayenne.module.chat.manager.interf

import arch.cayenne.lib.websocket.chat.data.ChatEnterRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLeaveRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountResponse
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.chat.data.MsgType
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketConnectState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * @author: wenxi
 * @date: 27/9/25 14:44
 * @description:
 */
interface ChatManagerFactory {
    suspend fun startChatServer(scope: CoroutineScope): ConnectState?
    suspend fun disConnectChatServer(scope: CoroutineScope): Boolean
    suspend fun chatLogin(chatType: ChatType): ChatLoginResponseData?
    suspend fun checkBetAmount(): CheckBetAmountResponse?
    suspend fun enterRoom(matchId: Long,chatType: ChatType): ChatEnterRoomResponse?
    suspend fun leaveRoom(matchId: Long,chatType: ChatType): ChatLeaveRoomResponse?
    suspend fun sendMsgToServer(
        matchId: Long,
        content: String,
        chatType: ChatType,
        msgType: MsgType,
        extraData: Map<String,String>?,
        refUid: List<Long>?,
    ): ChatSendMsgResponse?

    suspend fun registerMsgFlowToServer(): Flow<MsgNotify>
    suspend fun getChatHistory(
        roomId: Long,
        page: Int,
        pageSize: Int,
        requestId:String = ""
    ): GetChatHistoryResponse?

    suspend fun addLocalMsg(loginValue: ChatLoginResponseData, content: String,
                            msgType: MsgType,
                            extraData: Map<String,String>?,
                            chatType: ChatType,
                            refUid: List<Long>? = null,
                            refInfos: Map<Long,ChatRefUser>? = null
    ): ChatMsg
    suspend fun serverConnectFlow(): StateFlow<SocketConnectState>
}