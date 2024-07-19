package com.cn.game.sdk2.utils

import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.TAG
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

    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(view:View): Int {
        val insets = ViewCompat.getRootWindowInsets(view)
        if (insets != null) {
            val top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val height = bottom
            LogUtils.d(TAG,"getNavigationBarHeight-->top:$top,bottom:$bottom")
            return  height
        }
        return 0
    }
}