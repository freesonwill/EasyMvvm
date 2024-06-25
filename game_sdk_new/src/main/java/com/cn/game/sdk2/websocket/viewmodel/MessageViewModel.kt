package com.cn.game.sdk2.websocket.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.cn.game.sdk2.websocket.GameSocketClient
import kotlinx.coroutines.Dispatchers

class MessageViewModel(private val client: GameSocketClient) : ViewModel() {

    private val  _byte = MutableLiveData<ByteArray>()

    val data: LiveData<Array<Any?>?> = liveData(Dispatchers.Main) {
        val result = doConvert()
        emit(result)
    }

    private fun doConvert(): Array<Any?>? {
        return client.newUnpack(_byte.value)
    }

    fun setData(data:ByteArray){
        _byte.value = data
    }
}