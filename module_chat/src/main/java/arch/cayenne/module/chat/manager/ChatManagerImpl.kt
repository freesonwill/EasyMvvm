package arch.cayenne.module.chat.manager


import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
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
import arch.cayenne.lib.websocket.chat.data.ReportUserResponse
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.data.repository.LiveChatRepository
import arch.cayenne.module.chat.manager.interf.ChatManagerFactory
import game.chat.proto.GameChat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.cancellation.CancellationException


/**
 * @author: wenxi
 * @date: 27/9/25 14:41
 * @description: 实现聊天服务相关功能
 */
class ChatManagerImpl(private val chatRepo: LiveChatRepository) :
    ChatManagerFactory {
    private val TAG = this.javaClass.simpleName


    override suspend fun startChatServer(scope: CoroutineScope): ConnectState? {
        val state = chatRepo.getConnectStateFlow().value
        "startChatServer $state".logd(TAG)
        if (state != SocketConnectState.None && state != SocketConnectState.Closed) {
            return ConnectState.ConnectSuccess
        }
        return chatRepo.startSocket(scope)
    }

    override suspend fun disConnectChatServer(scope: CoroutineScope): Boolean {

        try {
            val value = chatRepo.disconnect(scope)
            "chat disconnect viewModel $value".logd(TAG)
            return value
        } catch (e: CancellationException) {
            "chat disconnect viewModel canceled".logi(TAG)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override suspend fun chatLogin(chatType: ChatType): ChatLoginResponseData? {
        return chatRepo.login(chatType)
    }

    override suspend fun checkBetAmount(): CheckBetAmountResponse? {
        return chatRepo.checkBetAmount()
    }

    override suspend fun enterRoom(matchId: Long, chatType: ChatType): ChatEnterRoomResponse? {
        return chatRepo.enterRoom(matchId,chatType)
    }

    override suspend fun leaveRoom(matchId: Long, chatType: ChatType): ChatLeaveRoomResponse? {
        return chatRepo.leaveRoom(matchId,chatType)
    }

    override suspend fun sendMsgToServer(
        matchId: Long,
        content: String,
        chatType: ChatType,
        msgType: MsgType,
        extraData: Map<String,String>?,
        refUid: List<String>?,
    ): ChatSendMsgResponse? {
        return chatRepo.sendMsg(matchId, content,  chatType, msgType, extraData,refUid)
    }

    override suspend fun registerMsgFlowToServer(): Flow<MsgNotify> {
        return chatRepo.registerNotifyMsg()
    }


    override suspend fun getChatHistory(
        roomId: Long,
        page: Int,
        pageSize: Int,
        requestId: String
    ): GetChatHistoryResponse? {

        return chatRepo.getChatHistory(roomId, page, pageSize, requestId)
    }

    override suspend fun addLocalMsg(
        loginValue: ChatLoginResponseData,
        content: String,
        msgType: MsgType,
        extraData: Map<String,String>?,
        chatType: ChatType,
        refUid: List<String>?,
        refInfos: Map<String, ChatRefUser>?
    ): ChatMsg {
        val id = System.currentTimeMillis().toString()
        val msg = ChatMsg(
            uid = loginValue.uid ?: "",
            userName = loginValue.username ?: "",
            avatarId = loginValue.avatarId ?: 0,
            content = content,
            msgId = id,
            timestamp = id,
            refUids = null,
            onlyForSelf = 0,
            replaceUserName = "",
            msgType = msgType,
            extraData = null,
            chatType = chatType
        )
        return msg
    }

    override suspend fun serverConnectFlow(): StateFlow<SocketConnectState> {
        return chatRepo.getConnectStateFlow()
    }

    override suspend fun reportUser(
        uid: String,
        chatType: ChatType,
        type: Int
    ): ReportUserResponse? {
       return chatRepo.reportUser(uid, chatType, type)
    }

    fun getLoginFlow(): StateFlow<ChatLoginResponseData?> {
        return chatRepo.getLoginFlow()
    }

}