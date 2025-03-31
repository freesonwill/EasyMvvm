package com.walisport.lib_common.data

import com.tencent.mmkv.MMKV

class UserDataManager {

    private val mmkv = MMKV.defaultMMKV()

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

            is Long -> {
                mmkv.putLong(key, value)
            }

            is Float -> {
                mmkv.putFloat(key, value)
            }
        }
    }
}