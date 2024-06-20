package com.luxury.lib_base.utils.ext

import android.os.Looper
import com.luxury.lib_base.utils.PinyinUtils


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 16:52
 **/
object CommonExt {

    fun Int.toPinyin(): String {
        return PinyinUtils.toPinyin(this)
    }

    //是否是主线程
    inline val isMainThread: Boolean get() {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}