package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.websocket.chat.data.ChatEnterRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLeaveRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.CheckBetAmountResponse
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.data.ConnectState
import com.walisport.module.live.LiveRemoteChatManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class LiveChatRepository(val remote: LiveRemoteChatManager) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun startSocket(): ConnectState {
        return remote.startSocket()
    }

    suspend fun disconnect(): Boolean {
        return remote.disConnect()
    }

    suspend fun login(): ChatLoginResponseData? {
        return remote.login()
    }

    suspend fun enterRoom(matchId: Long): ChatEnterRoomResponse? = remote.enterChatRoom(matchId)

    suspend fun leaveRoom(matchId: Long): ChatLeaveRoomResponse? = remote.leaveChatRoom(matchId)

    suspend fun sendMsg(
        roomId: Long,
        content: String,
        refUid: String? = null,
        refPlatform: Int? = null
    ): ChatSendMsgResponse? = remote.sendMsgNotify(roomId, content, refUid, refPlatform)

    suspend fun registerNotifyMsg(): Flow<MsgNotify> = remote.msgNotify()

    suspend fun checkBetAmount():CheckBetAmountResponse? = remote.checkBetAmount()

    suspend fun getChatHistory(roomId: Long, page: Int, pageSize: Int, requestId: String = ""):GetChatHistoryResponse? = remote.getChatHistory(roomId, page, pageSize, requestId)

}