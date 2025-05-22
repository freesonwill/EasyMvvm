package com.walisport.module.live.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * 是否静音的管理器
 */
class MuteManager {

    private val _liveData: MutableLiveData<Boolean> = MutableLiveData(false)

    val mutedLiveData: LiveData<Boolean> = _liveData

    /**
     * 改变静音状态
     */
    suspend fun changeMuteStatus() {
        //默认不静音
        _liveData.value = _liveData.value?.not() ?: true
    }

    /**
     * 静音
     */
    suspend fun mute() {
        _liveData.value = true
    }

    /**
     * 取消静音
     */
    suspend fun unMute() {
        _liveData.value = false
    }
}