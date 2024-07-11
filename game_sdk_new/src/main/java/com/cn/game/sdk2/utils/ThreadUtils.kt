package com.cn.game.sdk2.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/18 15:37
 **/
object ThreadUtils {
    private val HANDLER by lazy { Handler(Looper.getMainLooper()) }

    /**
     * 运行在主线程
     */
    fun runOnUiThread(delay:Long = 0,runnable: Runnable) {
        if(delay > 0) { HANDLER.postDelayed(runnable,delay); return }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            HANDLER.post(runnable)
        }
    }

    /**
     * 移除主线程的Callbacks
     */
    fun removeCallbacks(runnable: Runnable){
        HANDLER.removeCallbacks(runnable)
    }

    /**
     * 主线程Scope，提供给没有LifecycleScope，ViewModelScope的场景
     */
    val mainScope by lazy { CoroutineScope(Dispatchers.Main) }
}