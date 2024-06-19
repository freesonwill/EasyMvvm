package com.cn.game.sdk2.utils

import android.os.Looper


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 16:52
 **/
object Ext {
    fun Int.toPinyin(): String {
        return PinyinUtils.toPinyin(this)
    }

    //是否是主线程
    val isMainThread: Boolean get() {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}