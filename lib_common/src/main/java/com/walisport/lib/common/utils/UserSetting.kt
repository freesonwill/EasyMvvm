package com.walisport.lib.common.utils

import android.content.Context
import com.tencent.mmkv.MMKV

/**
 * 用户数据操作Singleton
 */

class UserSetting private constructor() {

    companion object {
        private var instance: UserSetting? = null

        fun getInstance(): UserSetting {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = UserSetting()
                    }
                }
            }
            return instance!!
        }
    }

    //初始化
    fun initializeMMKV(mContext: Context) {
        MMKV.initialize(mContext)
    }

    //设置昵称
    fun setNickName(nickName: String) {
        val mmkv = MMKV.defaultMMKV()
        mmkv?.encode("NickName", nickName)
    }

    //获取昵称
    fun getNickName(): String {
        val mmkv = MMKV.defaultMMKV()
        return mmkv.decodeString("NickName") ?: ""
    }
}