package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.websocket.data.ConnectState
import com.walisport.module.live.data.repository.LiveChatRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveChatViewModel : BaseViewModel() {
    private val chatRepository: LiveChatRepository by inject { parametersOf(viewModelScope) }

    fun startChatServer() {
        viewModelScope.launch {
            val result = chatRepository.startSocket()
            if (result == ConnectState.ConnectSuccess) {
                chatRepository.login()
            }
        }
    }

    fun disConnectChatServer() {
        viewModelScope.launch {
            chatRepository.disconnect()
        }
    }
}