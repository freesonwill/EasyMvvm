package com.cn.game.sdk2.utils

import android.os.Handler
import android.os.Looper

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/18 15:37
 **/
object ThreadUtils {
    private val HANDLER by lazy { Handler(Looper.getMainLooper()) }

    fun runOnUiThread(delay:Long = 0,runnable: Runnable) {
        if(delay > 0) { HANDLER.postDelayed(runnable,delay); return }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            HANDLER.post(runnable)
        }
    }
}