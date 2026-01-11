package arch.cayenne.module.chat.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.websocket.chat.data.ChatEnterRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLeaveRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountResponse
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.chat.data.MsgType
import arch.cayenne.lib.websocket.chat.data.ReportUserResponse
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.RemoteChatManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class LiveChatRepository(val remote: RemoteChatManager) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun startSocket(scope: CoroutineScope): ConnectState? {
        return remote.startSocket(scope)
    }

    suspend fun disconnect(scope: CoroutineScope): Boolean {
        return remote.disConnect(scope)
    }

    suspend fun login(chatType: ChatType): ChatLoginResponseData? {
        return remote.login(chatType)
    }

    suspend fun enterRoom(matchId: Long, chatType: ChatType): ChatEnterRoomResponse? =
        remote.enterChatRoom(matchId, chatType)

    suspend fun leaveRoom(matchId: Long, chatType: ChatType): ChatLeaveRoomResponse? =
        remote.leaveChatRoom(matchId, chatType)

    suspend fun sendMsg(
        roomId: Long,
        content: String,
        chatType: ChatType,
        msgType: MsgType,
        extraData: Map<String,String>? = null,
        refUid: List<String>?,
    ): ChatSendMsgResponse? = remote.sendMsgNotify(roomId, content,  chatType, msgType, extraData,refUid)

    suspend fun registerNotifyMsg(): Flow<MsgNotify> = remote.msgNotify()

    suspend fun checkBetAmount(): CheckBetAmountResponse? = remote.checkBetAmount()

    suspend fun reportUser(
        uid: String,
        chatType: ChatType,
        type: Int
    ): ReportUserResponse? = remote.reportUser(uid, chatType, type)

    suspend fun getChatHistory(
        roomId: Long,
        page: Int,
        pageSize: Int,
        requestId: String = ""
    ): GetChatHistoryResponse? = remote.getChatHistory(roomId, page, pageSize, requestId)

    fun getConnectStateFlow(): StateFlow<SocketConnectState> = remote.getConnectStateFlow()

    fun getLoginFlow(): StateFlow<ChatLoginResponseData?>{
        return remote.getLoginFlow()
    }

}