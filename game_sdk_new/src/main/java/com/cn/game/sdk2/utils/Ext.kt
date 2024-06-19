package com.cn.game.sdk2.utils

import android.os.Looper
import android.view.View


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
    inline val isMainThread: Boolean get() {
        return Looper.myLooper() == Looper.getMainLooper()
    }
    //是否在View区域内
    fun View.isInArea(rawX:Float,rawY:Float):Boolean{
        val rawXY = IntArray(2)
        getLocationOnScreen(rawXY)
        return rawX >= rawXY[0] && rawX <= (rawX + width) && rawY >= rawXY[1] && rawY <= (rawXY[1] + height)
    }
}