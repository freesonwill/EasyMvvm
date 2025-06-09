package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.chat.data.ChatRequestCodeEnum
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.data.ConnectState
import com.google.gson.Gson
import com.walisport.module.live.data.constants.CheckBetResultEnum
import com.walisport.module.live.data.repository.LiveChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LiveChatViewModel(private val chatRepo: LiveChatRepository) : BaseViewModel() {
    private val TAG = this@LiveChatViewModel.javaClass.simpleName
    private var matchId: Long? = null
    private val _softKeyBoardListener = MutableLiveData<Boolean>()
    private val _loginLiveData = MutableLiveData<ChatLoginResponseData?>()
    private val _sendMsgResultLiveData = MutableLiveData<ChatSendMsgResponse?>()
    private val _sendMsgLiveData = MutableLiveData<String>()
    private val _historyLiveData = MutableLiveData<Boolean>()
    private val _newMsgFLow = MutableStateFlow<MsgNotify?>(null)
    private val _checkBetAmountLiveData = MutableLiveData<CheckBetResultEnum>()

    //检查是否可以发送消息
    var checkBetAmountLiveData: LiveData<CheckBetResultEnum> = _checkBetAmountLiveData

    //消息列表
    val msgLists: MutableList<ChatMsg> = mutableListOf()

    //键盘是否显示中
    val softKeyBoardListener: LiveData<Boolean> = _softKeyBoardListener

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
     * 校验投注额
     * */
    fun startChatServer() {
        viewModelScope.launch {
            val state = chatRepo.startSocket()
            if (state == ConnectState.ConnectSuccess) {
                chatLogin()
            }
        }
    }

    /**
     * 关闭聊天服务
     * */
    private fun disConnectChatServer() {
        viewModelScope.launch {
            chatRepo.disconnect()
        }
    }

    /**
     * 聊天登陆
     * */
    private fun chatLogin() {
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
            disConnectChatServer()
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
     *检验投注额
     * */
    fun checkBetAmount() {
        viewModelScope.launch {
            val code = chatRepo.checkBetAmount()?.code
            _checkBetAmountLiveData.value = CheckBetResultEnum.getCheckBetResult(code ?: -1)
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
            val resp = chatRepo.getChatHistory(matchId!!, 1, 10)
            resp?.msgs?.let {
                msgLists.clear()
                msgLists.addAll(it)
            }
            _historyLiveData.value = resp?.msgs == null
        }
    }


    /**
     *更新软件盘显示
     * */
    fun updateSoftKeyBoard(isVisible: Boolean) {
        _softKeyBoardListener.value = isVisible
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


}