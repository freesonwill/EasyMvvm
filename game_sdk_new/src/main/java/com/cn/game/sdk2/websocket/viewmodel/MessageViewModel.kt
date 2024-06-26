package com.cn.game.sdk2.websocket.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.cn.game.sdk2.websocket.GameSocketClient
import com.cn.game.sdk2.websocket.bean.SendDataBean
import kotlinx.coroutines.Dispatchers

class MessageViewModel : ViewModel() {

    private val _byte = MutableLiveData<ByteArray>()
    private val _sendByte = MutableLiveData<SendDataBean>()

    val data: LiveData<ByteArray>
        get() = _byte

    val sendData: LiveData<SendDataBean>
        get() = _sendByte

    fun setData(data: ByteArray) {
        _byte.postValue(data)
    }

    fun setSendData(data: SendDataBean) {
        _sendByte.postValue(data)
    }
}