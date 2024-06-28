package com.cn.game.sdk2.ui.helper

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.cn.game.sdk2.ui.view.Fast3Toast

/**
 * 快3Toast辅助类
 */
object Fast3ToastHelper {
    private var _instance:Fast3Toast? = null

    fun init(context:Context,anchorView:ConstraintLayout){
        _instance = Fast3Toast(context,anchorView)
    }

    fun showToastNormal(msg: CharSequence, duration: Long = 2_000) {
        _instance?.showToastNormal(msg,duration)
    }

    fun dismissToast() {
        _instance?.dismissToast()
    }

    fun destroy(){
        _instance = null
    }
}