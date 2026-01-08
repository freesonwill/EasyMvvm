package arch.cayenne.module.chat.manager

import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.data.ChatEnterRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLeaveRoomResponse
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.ChatRefUser
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.chat.data.GetChatHistoryResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.chat.data.MsgType
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.data.constants.CheckBetResultEnum
import com.google.gson.Gson
import game.chat.proto.GameChat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * @author: wenxi
 * @date: 27/9/25 14:42
 * @description: chat聊天相关api监听
 */
class ChatServerController(
    private val scope: CoroutineScope,
    private val manager: ChatManagerImpl
) {

    val TAG = this.javaClass.simpleName
    private val _sendMsgResultFlow = MutableStateFlow<ChatSendMsgResponse?>(null)
    private val _historyFlow = MutableSharedFlow<GetChatHistoryResponse?>(replay = 1, extraBufferCapacity = 2)
    private val _checkBetAmountFlow = MutableStateFlow<CheckBetResultEnum?>(null)
    private val _enterRoomFlow = MutableSharedFlow<ChatEnterRoomResponse?>(1, extraBufferCapacity = 2)
    private val _leaveRoomFlow = MutableSharedFlow<ChatLeaveRoomResponse?>(1, extraBufferCapacity = 2)

    //检查是否可以发送消息
    var checkBetAmountFlow: StateFlow<CheckBetResultEnum?> = _checkBetAmountFlow

    //进入聊天室返回结果
    val enterRoomFlow: SharedFlow<ChatEnterRoomResponse?> = _enterRoomFlow

    //离开聊天室返回结果
    val leaveRoomFlow: SharedFlow<ChatLeaveRoomResponse?> = _leaveRoomFlow

    //消息发送返回结果
    val sendMsgResultFlow: StateFlow<ChatSendMsgResponse?> = _sendMsgResultFlow

    //查询历史消息返回结果
    val historyFlow: SharedFlow<GetChatHistoryResponse?> = _historyFlow

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

    fun chatLogin(chatType: ChatType) {
        "chatLogin is null ${getManagerLoginFlow().value == null}".logd(TAG)
        if (getManagerLoginFlow().value != null) {
            return
        }
        scope.launch(Dispatchers.IO) {
            manager.chatLogin(chatType)
        }
    }

    fun enterRoom(matchId: Long, chatType: ChatType) {
        this.matchId = matchId
        scope.launch(Dispatchers.IO) {
            val value = manager.enterRoom(matchId, chatType)
            _enterRoomFlow.emit(value)
            checkBetAmount()
            getChatHistory(matchId, 1, 20)
        }

    }

    fun checkBetAmount() {
        scope.launch(Dispatchers.IO) {
            val result = manager.checkBetAmount()
            val value = CheckBetResultEnum.getCheckBetResult(result?.code ?: -1)

            _checkBetAmountFlow.emit(value)
        }
    }

    fun leaveRoom(matchId: Long, chatType: ChatType) {
        scope.launch(Dispatchers.IO) {
            val value = manager.leaveRoom(matchId, chatType)
            _leaveRoomFlow.emit(value)
        }
    }

    fun sendMsgToServer(
        matchId: Long,
        content: String,
        chatType: ChatType,
        msgType: MsgType,
        extraData: Map<String, String>?,
        refUid: List<String>?,
    ) {
        scope.launch(Dispatchers.IO) {
            val value =
                manager.sendMsgToServer(matchId, content, chatType, msgType, extraData, refUid)
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

    fun addLocalMsg(
        content: String,
        chatType: ChatType,
        msgType: MsgType,
        extraData: Map<String, String>?,
        refUid: List<String>?,
        refInfos: Map<String, ChatRefUser>?
    ): ChatMsg? {
        if (getManagerLoginFlow().value == null) {
            "login is null".logd(TAG)
            return null
        }
        val id = System.currentTimeMillis().toString()
        val loginValue = getManagerLoginFlow().value
        val msg = ChatMsg(
            uid = loginValue?.uid ?: "",
            userName = loginValue?.username ?: "",
            avatarId = loginValue?.avatarId ?: 0,
            content = content,
            msgId = id,
            timestamp = id,
            refUids = refUid,
            refInfos = refInfos,
            onlyForSelf = 0,
            replaceUserName = "",
            msgType = msgType,
            extraData = extraData,
            chatType = chatType,
        )
//        "addLocalMsg msg=${Gson().toJson(msg)}".logd(TAG)
        return msg
    }

    fun getManagerLoginFlow(): StateFlow<ChatLoginResponseData?> {
        return manager.getLoginFlow()
    }


}