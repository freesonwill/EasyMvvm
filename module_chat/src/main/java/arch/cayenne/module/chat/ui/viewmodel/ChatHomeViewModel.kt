package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.data.constants.CheckBetResultEnum
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.manager.ChatServerController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class ChatHomeViewModel() : BaseViewModel() {
    private var matchId: Long? = null

    //    private val _currentSoftKeyboard = MutableStateFlow(KeyBoardType.CHAT)
    private val _updateKeyboardUiStatus = MutableLiveData(KeyBoardType.CHAT)
    private val _sendMsgLiveData = MutableLiveData<String>()
    private val _chatHistoryIsEmpty = MutableLiveData<Boolean>()
    private val chatServer: ChatServerController by inject { parametersOf(viewModelScope) }

    var currentKeyBoardType:KeyBoardType = KeyBoardType.CHAT
    //整个表情键盘页面的整体高度
    var keyBoardHeight: Int = 0
    //键盘发送过来的消息
    val sendMsgLiveData: LiveData<String> = _sendMsgLiveData
    //更新键盘盘状态
    val updateKeyboardUiStatus: LiveData<KeyBoardType> = _updateKeyboardUiStatus
    //判断聊天记录是不是空的
    val chatHistoryIsEmpty:LiveData<Boolean> = _chatHistoryIsEmpty

    //聊天api相关
    val chatHistoryFlow = chatServer.historyFlow
    val sendMsgToServerFlow = chatServer.sendMsgResultFlow
    val loginFlow = chatServer.loginFlow
    val checkBetAmountFlow = chatServer.checkBetAmountFlow

    fun setArguments(matchId: Long?) {
        //直播间重新从联赛进入时，刷新matchId 重新进入聊天室
        if (this.matchId != null && this.matchId != matchId) {
            this.matchId = matchId
            chatServer.enterRoom(matchId!!)
        } else {
            this.matchId = matchId
        }
    }

    /**
     * 开启聊天服务
     * */
    fun startChatServer() {
        chatServer.connectChatServer()
    }

    /**
     * 关闭聊天服务
     * */
    fun disConnectChatServer() {
        chatServer.disconnectChatServer()
    }

    suspend fun serverFlow():StateFlow<SocketConnectState>{
        return chatServer.serverConnectFlow()
    }

    suspend fun registerNewMsgFlow(): Flow<MsgNotify> {
        return chatServer.registerMsgFlowToServer()
    }

    /**
     * 聊天登陆
     * */
    fun chatLogin() {
        matchId?.let {
            chatServer.chatLogin(it)
        }
    }

    /**
     *推出聊天室
     * */
    fun leaveRoom() {
        chatServer.leaveRoomFlow
    }

    /**
     *发送消息
     * */
    fun sendMsgToServer(content: String, refUid: String? = null, refPlatform: Int? = null) {
        matchId?.let {
            chatServer.sendMsgToServer(it, content, refUid, refPlatform)
        }
    }

    /**
     * 弹出软件盘 表情键盘时检查是否可以继续弹出对应键盘
     * */
    fun checkSoftKeyboardVisible(): Boolean {
        return when (chatServer.checkBetAmountFlow.value) { //聊天权限不足时每弹出都需要检查权限
            CheckBetResultEnum.BET_AMOUNT_INVALID, CheckBetResultEnum.BALANCE_INVALID -> {
                false
            }
            CheckBetResultEnum.SUCCESS -> true
            null -> false
        }
    }


    /**
     * 添加本地数据
     * */
    fun addLocalMsg(content: String):ChatMsg? {
        if (loginFlow.value == null) {
            "chat is not login ".logd(TAG)
            return null
        }
        val msg = chatServer.addLocalMsg(content)
       return msg
    }

    /**
     * 软件et传递消息
     * */
    fun sendMsgToChat(msg: String) {
        if (msg.isEmpty()) {
            return
        }
        _sendMsgLiveData.value = msg
    }

    fun updateKeyBoardUi(keyBoardType: KeyBoardType, flag: Int) {
        _updateKeyboardUiStatus.value = keyBoardType
    }

    fun refreshChatUi(value:Boolean){
        _chatHistoryIsEmpty.value = value
    }



}