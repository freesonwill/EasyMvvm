package arch.cayenne.module.chat.manager

import arch.cayenne.lib.websocket.chat.data.ChatEnterRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLeaveRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.data.constants.CheckBetResultEnum
import arch.cayenne.module.chat.data.constants.MsgType
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @author: wenxi
 * @date: 27/9/25 14:42
 * @description:
 */
class ChatServerController(
    private val scope: CoroutineScope,
    private val manager: ChatManagerImpl
) {

    private val _loginFlow = MutableStateFlow<ChatLoginResponseData?>(null)
    private val _sendMsgResultFlow = MutableStateFlow<ChatSendMsgResponse?>(null)
    private val _historyFlow = MutableStateFlow<GetChatHistoryResponse?>(null)
    private val _checkBetAmountFlow = MutableStateFlow<CheckBetResultEnum?>(null)
    private val _enterRoomFlow = MutableStateFlow<ChatEnterRoomResponse?>(null)
    private val _leaveRoomFlow = MutableStateFlow<ChatLeaveRoomResponse?>(null)

    //检查是否可以发送消息
    var checkBetAmountFlow: StateFlow<CheckBetResultEnum?> = _checkBetAmountFlow


    //登陆返回数据
    val loginFlow: StateFlow<ChatLoginResponseData?> = _loginFlow

    //进入聊天室返回结果
    val enterRoomFlow: StateFlow<ChatEnterRoomResponse?> = _enterRoomFlow

    //离开聊天室返回结果
    val leaveRoomFlow: StateFlow<ChatLeaveRoomResponse?> = _leaveRoomFlow

    //消息发送返回结果
    val sendMsgResultFlow: StateFlow<ChatSendMsgResponse?> = _sendMsgResultFlow

    //查询历史消息返回结果
    val historyFlow: StateFlow<GetChatHistoryResponse?> = _historyFlow

    var newMsgNotify: Flow<MsgNotify?> = MutableStateFlow(null)
    var matchId: Long? = null

    fun connectChatServer() {
        scope.launch(Dispatchers.IO) {
            val state = manager.startChatServer(scope)
        }
    }

    fun disconnectChatServer() {
        scope.launch {
            manager.disConnectChatServer(scope)
        }
    }

    suspend fun serverConnectFlow(): StateFlow<SocketConnectState> {
        return manager.serverConnectFlow()
    }

    fun chatLogin(matchId: Long) {
        scope.launch(Dispatchers.IO) {
            val value = manager.chatLogin()
            _loginFlow.emit(value)
            enterRoom(matchId)
        }
    }

    fun enterRoom(matchId: Long) {
        this.matchId = matchId
        scope.launch(Dispatchers.IO) {
            val value = manager.enterRoom(matchId)
            _enterRoomFlow.emit(value)
            checkBetAmount()
            getChatHistory(matchId, 1, 100)
        }

    }

    fun checkBetAmount() {
        scope.launch(Dispatchers.IO) {
            val result = manager.checkBetAmount()
            val value = CheckBetResultEnum.getCheckBetResult(result?.code ?: -1)

            _checkBetAmountFlow.emit(value)
        }
    }

    fun leaveRoom(matchId: Long) {
        scope.launch(Dispatchers.IO) {
            val value = manager.leaveRoom(matchId)
            _leaveRoomFlow.emit(value)
        }
    }

    fun sendMsgToServer(
        matchId: Long,
        content: String,
        refUid: String? = null,
        refPlatform: Int? = null
    ) {
        scope.launch(Dispatchers.IO) {
            val value = manager.sendMsgToServer(matchId, content, refUid, refPlatform)
            _sendMsgResultFlow.emit(value)
        }
    }

    suspend fun registerMsgFlowToServer(): Flow<MsgNotify> {
        return manager.registerMsgFlowToServer()
    }

    fun getChatHistory(
        roomId: Long,
        page: Int,
        pageSize: Int,
        requestId: String = ""
    ) {
        scope.launch(Dispatchers.IO) {
            val value = manager.getChatHistory(roomId, page, pageSize, requestId)
            _historyFlow.emit(value)
        }
    }

    fun addLocalMsg(content: String): ChatMsg? {
        if (loginFlow.value == null) {
            return null
        }
        val id = System.currentTimeMillis().toString()
        val loginValue = _loginFlow.value
        val msg = ChatMsg(
            uid = loginValue?.uid.toString(),
            userName = loginValue?.username ?: "",
            avatarId = loginValue?.avatarId ?: 0,
            content = content,
            msgId = id,
            timestamp = id,
            refUid = "",
            refAvatarId = 0,
            refUserName = "",
            onlyForSelf = 0,
            platform = 5
        )
        return msg
    }

}