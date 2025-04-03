package com.walisport.lib.common.data

import com.tencent.mmkv.MMKV

class UserDataManager {

    private val mmkv = MMKV.defaultMMKV()

    fun <T> setKeyValue(key: UserDataKey, value: T) {
        when (value) {
            is String -> {
                mmkv.putString(key.key, value)
            }

            is Boolean -> {
                mmkv.putBoolean(key.key, value)
            }

            is Int -> {
                mmkv.putInt(key.key, value)
            }

            is Long -> {
                mmkv.putLong(key.key, value)
            }

            is Float -> {
                mmkv.putFloat(key.key, value)
            }
        }
    }

    fun getStringValue(key: UserDataKey, default: String): String {
        return mmkv.getString(key.key, default) ?: default
    }
}