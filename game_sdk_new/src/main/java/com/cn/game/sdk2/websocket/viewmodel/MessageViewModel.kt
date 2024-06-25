package com.cn.game.sdk2.websocket.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.cn.game.sdk2.websocket.GameSocketClient
import kotlinx.coroutines.Dispatchers

class MessageViewModel : ViewModel() {

    private val _byte = MutableLiveData<ByteArray>()

    val data: LiveData<ByteArray>
        get() = _byte

    fun setData(data: ByteArray) {
        _byte.postValue(data)
    }
}