package com.cn.game.sdk2.utils

import java.util.Locale

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/27 17:05
 **/
object CommonUtils {


    @JvmOverloads
    fun formatSeconds(totalSeconds: Int, format: String = "%02d:%02d"): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.ROOT, format, minutes, seconds)
    }

}