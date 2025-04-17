package com.walisport.module.live.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * 是否静音的管理器
 */
class MuteManager {

    private val _liveData: MutableLiveData<Boolean> = MutableLiveData(false)

    val mutedLiveData: LiveData<Boolean> = _liveData

    suspend fun changeMuteStatus() {
        //默认不静音
        _liveData.value = _liveData.value?.not() ?: true
    }
}