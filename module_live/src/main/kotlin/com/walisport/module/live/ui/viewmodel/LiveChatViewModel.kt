package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.data.ChatLoginResponseData
import arch.cayenne.lib.websocket.chat.data.ChatRequestCodeEnum
import arch.cayenne.lib.websocket.chat.data.ChatSendMsgResponse
import arch.cayenne.lib.websocket.chat.data.MsgNotify
import arch.cayenne.lib.websocket.data.ConnectState
import com.walisport.module.live.data.repository.LiveChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LiveChatViewModel(private val chatRepo: LiveChatRepository) : BaseViewModel() {
    private val TAG = this@LiveChatViewModel.javaClass.simpleName
    private var matchId: Long? = null
    private val _softKeyBoardListener = MutableLiveData<Boolean>()
    private val _loginLiveData = MutableLiveData<ChatLoginResponseData?>()
    private val _sendMsgResultLiveData = MutableLiveData<ChatSendMsgResponse?>()
    private val _msgLiveData = MutableLiveData<String>()
    val softKeyBoardListener:LiveData<Boolean> = _softKeyBoardListener
    val newMsgFlow = MutableStateFlow<MsgNotify?>(null)
    val loginLiveData: LiveData<ChatLoginResponseData?> = _loginLiveData
    val enterRoomLiveData = MutableLiveData<Boolean>()
    val leaveRoomLiveData = MutableLiveData<Boolean>()
    val sendMsgResultLiveData: LiveData<ChatSendMsgResponse?> = _sendMsgResultLiveData
    val msgLiveData:LiveData<String> = _msgLiveData
    var chatRoomId:Long = 0


    fun setArguments(matchId: Long?) {
        this.matchId = matchId
    }

    /**
     * 软件et传递消息
     * */
    fun sendMsgToChat(msg:String){
        _msgLiveData.value = msg
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
                 chatRoomId = it.chatroomId
                code == ChatRequestCodeEnum.SUCCESS.code
            } ?: false
            "enterRoom room result ${resp?.code} ${chatRoomId}".logd(TAG)
            checkBetAmount()
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
        if(matchId == null){
            return
        }
        viewModelScope.launch {
            _sendMsgResultLiveData.value = chatRepo.sendMsg(matchId!!, content, refUid, refPlatform)
        }
    }

    /**
     *监听新消息
     * */
     fun registerMsgFlow(){
       viewModelScope.launch {
           chatRepo.registerNotifyMsg().collect{
               newMsgFlow.value = it
           }
       }
    }

    /**
     *检验投注额
     * */
    fun checkBetAmount(){
        viewModelScope.launch {
            chatRepo.checkBetAmount()
        }
    }

    /**
     *更新软件盘显示
     * */
    fun updateSoftKeyBoard(isVisible:Boolean){
        _softKeyBoardListener.value = isVisible
    }



}