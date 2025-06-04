package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.data.repository.LiveChatRepository
import kotlinx.coroutines.launch

class LiveChatViewModel(private val chatRepo: LiveChatRepository) : BaseViewModel() {

    fun chatLogin(){
        viewModelScope.launch {
            chatRepo.login()
        }
    }


}