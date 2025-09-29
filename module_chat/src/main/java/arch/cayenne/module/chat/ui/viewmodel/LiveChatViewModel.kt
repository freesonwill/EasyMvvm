package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.database.dao.ChatConfigDao
import arch.cayenne.lib.database.entity.ChatConfigBean
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.CheckBetResultEnum
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.manager.ChatServerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveChatViewModel(private val userDataManager: UserDataManager) : BaseViewModel() {
    private var matchId: Long? = null

    //    private val _currentSoftKeyboard = MutableStateFlow(KeyBoardType.CHAT)
    private val _updateKeyboardUiStatus = MutableLiveData(KeyBoardType.CHAT)
    private val _sendMsgLiveData = MutableLiveData<String>()
    private val chatServer: ChatServerController by inject { parametersOf(viewModelScope) }


    //聊天设置
    private val chatConfigDao: ChatConfigDao by inject()

    //整个表情键盘页面的整体高度
    var keyBoardHeight: Int = 0

    //消息列表
    val msgLists: MutableList<ChatMsg> = mutableListOf()

//    //当前显示的键盘类型
//    val currentSoftKeyboard: StateFlow<KeyBoardType> = _currentSoftKeyboard

    //键盘发送过来的消息
    val sendMsgLiveData: LiveData<String> = _sendMsgLiveData

    //更新键盘盘状态
    val updateKeyboardUiStatus: LiveData<KeyBoardType> = _updateKeyboardUiStatus

    val toastLiveData: MutableLiveData<String> = MutableLiveData()

    //软件盘高度
    var softKeyBoardHeight: Int = 0

    //软件盘弹出时间
//    var softKeyBoardDuration: Long = 170L

    //软件盘状态 true 打开 false 关闭
    var softKeyboardStatus: Boolean = false

    //键盘点击的意向
    var clickKeyBoardType: KeyBoardType = KeyBoardType.CHAT

    //当前键盘状态
    var currentKeyBoardType: KeyBoardType = KeyBoardType.CHAT

    //判断是否开启app后第一次弹出软件盘
    var isFirstOpen: Boolean = true

    val chatHistoryFlow = chatServer.historyFlow
    val sendMsgToServerFlow = chatServer.sendMsgResultFlow
    val newMsgFlow = chatServer.newMsgFlow
    val loginFlow = chatServer.loginFlow

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
     *首次检查聊天权限投注额度和余额失败后
     * 每次点击软件盘都查询投注额 根据结果判断是否显示软件盘
     * */
    fun checkSoftKeyBoardBetAmount(keyBoardType: KeyBoardType, flag: Int = 0) {
        viewModelScope.launch {
            val checkBetAmountValue = chatServer.checkBetAmountLiveData.value

            when (checkBetAmountValue) {
                CheckBetResultEnum.BET_AMOUNT_INVALID -> {
                    toastLiveData.value = R.string.insufficient_bet_amount.getString()
                    clickKeyBoardType = KeyBoardType.CHAT
                }

                CheckBetResultEnum.BALANCE_INVALID -> {
                    toastLiveData.value = R.string.insufficient_balance.getString()
                    clickKeyBoardType = KeyBoardType.CHAT
                }

                CheckBetResultEnum.SUCCESS -> {
                    updateKeyBoardUi(keyBoardType, flag)
                }

                null -> {
                    toastLiveData.value = R.string.insufficient_bet_amount.getString()
                    clickKeyBoardType = KeyBoardType.CHAT
                }

            }

        }
    }

    /**
     * 获取历史聊天数据
     * */
    fun getChatHistory(list: List<ChatMsg>?) {
        if (list == null) {
            return
        }
        msgLists.clear()
        msgLists.addAll(list.reversed())
    }

    /**
     * 弹出软件盘 表情键盘时检查是否可以继续弹出对应键盘
     * */
    fun checkSoftKeyboardVisible(): Boolean {
        return when (chatServer.checkBetAmountLiveData.value) { //聊天权限不足时每弹出都需要检查权限
            CheckBetResultEnum.BET_AMOUNT_INVALID, CheckBetResultEnum.BALANCE_INVALID -> {
                false
            }

            CheckBetResultEnum.SUCCESS -> true
            null -> false
        }
    }

    /**
     * 添加新数据的chatlist
     * */
    fun addNewMsgs(msg: MsgNotify): List<ChatMsg> {
        msgLists.add(msgLists.size, msg.msg)
        return msgLists
    }

    /**
     * 添加本地数据
     * */
    fun addLocalMsg(content: String) {
        if (loginFlow.value == null) {
            "chat is not login aaa".logd(TAG)
            return
        }
        val msg = chatServer.addLocalMsg(content)
        msg?.let {
            msgLists.add(msgLists.size, msg)
        }
    }


    fun setKeyBoardHeight() {
        softKeyBoardHeight = userDataManager.getValue(UserDataKey.KEY_SOFT_KEYBOARD_HEIGHT, 0)
        checkFirstOpen()
    }

    private fun checkFirstOpen() {
        viewModelScope.launch(Dispatchers.IO) {
            isFirstOpen = (chatConfigDao.getFirst() ?: ChatConfigBean(
                0,
                true
            ).also { chatConfigDao.insertChatConfig(it) }).isFirstOpen
        }
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

    /**
     * 由于系统特性，当软件盘出现时，再次点击Editext软件盘会消失
     * 消失后会显示
     * */
    fun addSoftKeyBoardEvent(keyBoardType: KeyBoardType, flag: Int = 0) {
        if (keyBoardType == clickKeyBoardType) {
            return
        }
        this.clickKeyBoardType = keyBoardType
    }

    /**
     *更新软件盘显示
     * */
    fun updateKeyBoard() {
        if (currentKeyBoardType == clickKeyBoardType) {
            return
        }
        currentKeyBoardType = clickKeyBoardType
    }

    /**
     * 控制软件盘的开关
     * @param softKeyBoarVisible true显示软件盘  false 关闭软件盘
     * */
//    fun updateSoftKeyBoard(softKeyBoarVisible:Boolean,flag: Int){
//        if(softKeyBoarVisible == _openSoftKeyBoardLiveData.value){
//            return
//        }
//        _openSoftKeyBoardLiveData.value = softKeyBoarVisible
//    }


    fun saveUpdateSoftKeyBoardHeight() {
        userDataManager.setKeyValue(UserDataKey.KEY_SOFT_KEYBOARD_HEIGHT, softKeyBoardHeight)
//        userDataManager.setKeyValue(UserDataKey.KEY_SOFT_KEYBOARD_DURATION, softKeyBoardDuration)
    }

    fun updateKeyBoardUi(keyBoardType: KeyBoardType, flag: Int) {
        addSoftKeyBoardEvent(keyBoardType, flag)
        _updateKeyboardUiStatus.value = keyBoardType
    }

    fun setSoftFirstOpen(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(200) //防止动画还没有延迟，isFistOpen就false
            isFirstOpen = value
            chatConfigDao.updateConfigBean(ChatConfigBean(0, value))
        }
    }

}