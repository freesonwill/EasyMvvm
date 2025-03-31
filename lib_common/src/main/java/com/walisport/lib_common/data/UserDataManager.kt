package com.walisport.lib_common.data

import com.tencent.mmkv.MMKV

class UserDataManager {

    private val mmkv = MMKV.defaultMMKV()

    companion object {
        private var instance: UserDataManager? = null
    }

    fun <T> setKeyValue(key: String, value: T) {
        when (value) {
            is String -> {
                mmkv.putString(key, value)
            }

            is Boolean -> {
                mmkv.putBoolean(key, value)
            }

            is Int -> {
                mmkv.putInt(key, value)
            }
        }
    }
}