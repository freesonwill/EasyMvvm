package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.ChatRequestCodeEnum
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.data.SocketConnectState
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.CheckBetResultEnum
import com.walisport.module.live.data.constants.KeyBoardType
import com.walisport.module.live.data.repository.LiveChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LiveChatViewModel(private val chatRepo: LiveChatRepository) : BaseViewModel() {
    private var matchId: Long? = null
    private val _currentSoftKeyboard = MutableStateFlow(KeyBoardType.CHAT)
    private val _loginLiveData = MutableLiveData<ChatLoginResponseData?>()
    private val _sendMsgResultLiveData = MutableLiveData<ChatSendMsgResponse?>()
    private val _sendMsgLiveData = MutableLiveData<String>()
    private val _historyLiveData = MutableLiveData<Boolean>()
    private val _newMsgFLow = MutableStateFlow<MsgNotify?>(null)
    private val _checkBetAmountLiveData = MutableLiveData<CheckBetResultEnum>()
    private val _softKeyBoardListener = MutableStateFlow(KeyBoardType.CHAT)
    private val _openSoftKeyBoardLiveData = MutableLiveData<Boolean>()

    //整个表情键盘页面的整体高度
    var keyBoardHeight: Int = 0

    //检查是否可以发送消息
    var checkBetAmountLiveData: LiveData<CheckBetResultEnum> = _checkBetAmountLiveData

    //消息列表
    val msgLists: MutableList<ChatMsg> = mutableListOf()

    //当前显示的键盘类型
    val currentSoftKeyboard: StateFlow<KeyBoardType> = _currentSoftKeyboard

    //监听新消息
    val newMsgFlow: Flow<MsgNotify?> = _newMsgFLow

    //登陆返回数据
    val loginLiveData: LiveData<ChatLoginResponseData?> = _loginLiveData

    //进入聊天室返回结果
    val enterRoomLiveData = MutableLiveData<Boolean>()

    //离开聊天室返回结果
    val leaveRoomLiveData = MutableLiveData<Boolean>()

    //消息发送返回结果
    val sendMsgResultLiveData: LiveData<ChatSendMsgResponse?> = _sendMsgResultLiveData

    //键盘发送过来的消息
    val sendMsgLiveData: LiveData<String> = _sendMsgLiveData

    //查询历史消息返回结果
    val historyLiveData: LiveData<Boolean> = _historyLiveData

    //监听LiveSoftKeyBoardFragment点击事件
    val softKeyBoardListener: StateFlow<KeyBoardType> = _softKeyBoardListener

    //打开软件盘
    val openSoftKeyBoardLiveData:LiveData<Boolean> = _openSoftKeyBoardLiveData

    val toastLiveData: MutableLiveData<String> = MutableLiveData()


    //软件盘高度
    var softKeyBoardHeight:Int = 0

    fun setArguments(matchId: Long?) {
        this.matchId = matchId
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
     * 聊天登陆
     * */
    fun chatLogin() {
        viewModelScope.launch {
            _loginLiveData.value = chatRepo.login()
        }
    }

    /**
     *进入聊天室
     * */
    fun enterRoom() {
        if (matchId == null) {
            return
        }
        viewModelScope.launch {
            val resp = chatRepo.enterRoom(matchId!!)
            enterRoomLiveData.value = resp?.let {
                val code = it.code
                code == ChatRequestCodeEnum.SUCCESS.code
            } ?: false
            checkBetAmount()
            registerMsgFlow()
            getChatHistory()
        }
    }

    /**
     *推出聊天室
     * */
    fun leaveRoom() {
        if (matchId == null) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val resp = chatRepo.leaveRoom(matchId!!)
            val flag = resp?.let {
                it.code == ChatRequestCodeEnum.SUCCESS.code
            } ?: false
            "leave room result $flag".logd(TAG)
        }
    }

    /**
     *发送消息
     * */
    fun sendMsgToServer(content: String, refUid: String? = null, refPlatform: Int? = null) {
        if (matchId == null) {
            return
        }
        viewModelScope.launch {
            _sendMsgResultLiveData.value = chatRepo.sendMsg(matchId!!, content, refUid, refPlatform)
        }
    }

    /**
     *监听新消息
     * */
    fun registerMsgFlow() {
        viewModelScope.launch {
            chatRepo.registerNotifyMsg().collect {
                _newMsgFLow.value = it
            }
        }
    }

    /**
     *首次进入时检验投注额
     * */
    private fun checkBetAmount() {
        viewModelScope.launch {
            val code = chatRepo.checkBetAmount()?.code
            _checkBetAmountLiveData.value = CheckBetResultEnum.getCheckBetResult(code ?: -1)
        }
    }

    /**
     *首次检查聊天权限投注额度和余额失败后
     * 每次点击软件盘都查询投注额 根据结果判断是否显示软件盘
     * */
    fun checkSoftKeyBoardBetAmount() {
        viewModelScope.launch {
            val code = chatRepo.checkBetAmount()?.code
            _checkBetAmountLiveData.value = CheckBetResultEnum.getCheckBetResult(code ?: -1)

            when (_checkBetAmountLiveData.value) {
                CheckBetResultEnum.BET_AMOUNT_INVALID -> {
                    toastLiveData.value = R.string.insufficient_bet_amount.getString()
                    _softKeyBoardListener.value = KeyBoardType.CHAT
                }
                CheckBetResultEnum.BALANCE_INVALID -> {
                    toastLiveData.value = R.string.insufficient_balance.getString()
                    _softKeyBoardListener.value = KeyBoardType.CHAT
                }
                CheckBetResultEnum.SUCCESS -> {
                    _currentSoftKeyboard.value = softKeyBoardListener.value
                }
                null -> {
                    toastLiveData.value = R.string.insufficient_fali.getString()
                    _softKeyBoardListener.value = KeyBoardType.CHAT
                }
            }
        }
    }

    /**
     * 获取历史聊天数据
     * */
    fun getChatHistory() {
        if (matchId == null) {
            return
        }
        viewModelScope.launch {
            val resp = chatRepo.getChatHistory(matchId!!, 1, 100)
            resp?.msgs?.let {
                msgLists.clear()

                msgLists.addAll(it.reversed())
            }
            _historyLiveData.value = resp?.msgs == null
        }
    }

    /**
     * 弹出软件盘 表情键盘时检查是否可以继续弹出对应键盘
     * */
     fun checkSoftKeyboardVisible(): Boolean {
        return when (checkBetAmountLiveData.value) { //聊天权限不足时每弹出都需要检查权限
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
        if (_loginLiveData.value == null) {
            return
        }
        val id = System.currentTimeMillis().toString()
        val user = _loginLiveData.value!!
        val msg = ChatMsg(
            uid = user.uid.toString(),
            userName = user.username ?: "",
            avatarId = user.avatarId ?: 0,
            content = content,
            msgId = id,
            timestamp = id,
            refUid = "",
            refAvatarId = 0,
            refUserName = "",
            onlyForSelf = 0,
            platform = 5
        )
        msgLists.add(msgLists.size, msg)
    }

    /**
     * 由于系统特性，当软件盘出现时，再次点击Editext软件盘会消失
     * 消失后会显示
     * */
    fun addSoftKeyBoardEvent(keyBoardType: KeyBoardType,flag:Int = 0) {

        if(keyBoardType == _softKeyBoardListener.value){
            return
        }
        _softKeyBoardListener.tryEmit(keyBoardType)
    }

    /**
     *更新软件盘显示
     * */
    fun updateKeyBoard() {
        if (currentSoftKeyboard.value == softKeyBoardListener.value) {
            return
        }
        _currentSoftKeyboard.value = softKeyBoardListener.value
    }

    /**
     * 控制软件盘的开关
     * @param softKeyBoarVisible true显示软件盘  false 关闭软件盘
     * */
    fun updateSoftKeyBoard(softKeyBoarVisible:Boolean,flag: Int){
//        if(softKeyBoarVisible == _openSoftKeyBoardLiveData.value){
//            return
//        }
        "updateSoftKeyBoard $softKeyBoarVisible  rflag $flag".logd("aaa")
        _openSoftKeyBoardLiveData.value = softKeyBoarVisible
    }

    fun getConnectStateFlow(): StateFlow<SocketConnectState> = chatRepo.getConnectStateFlow()

}