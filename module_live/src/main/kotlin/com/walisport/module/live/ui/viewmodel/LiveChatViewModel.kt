package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.chatwebsocket.ChatNativeLib
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketRequestData
import com.walisport.module.live.data.repository.LiveChatRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveChatViewModel : BaseViewModel() {
    val chatRepository: LiveChatRepository by inject { parametersOf(viewModelScope) }

    fun startChatServer() {
        viewModelScope.launch {
            val result = chatRepository.startSocket()
            "ChatSocketClientService startserver result $result".logd()
            if (result == ConnectState.ConnectSuccess) {
                delay(3000)
                startLogin()
            }
        }

//        val native = NativeLib()
//        val data = SocketRequestData(1,2,3,str.toByteArray())
//         val result = native.encrypt(data)
//        val deresult = native.decrypt(result!!)

//        val str = "123456"
//        val data = SocketRequestData(1,2,3,str.toByteArray(Charsets.UTF_8))
//        val chatNative = ChatNativeLib()
//        val chatResult = chatNative.encrypt(data)
//        val chatdeResult = chatNative.decrypt(chatResult!!)
////
//        "result   chatResult $chatResult chatderesult $chatdeResult".logi("liveChat")

    }

    fun disConnectChatServer() {
        viewModelScope.launch {
            chatRepository.disconnect()
        }
    }

    fun startLogin() {
        viewModelScope.launch {
            chatRepository.login()
        }
    }


}