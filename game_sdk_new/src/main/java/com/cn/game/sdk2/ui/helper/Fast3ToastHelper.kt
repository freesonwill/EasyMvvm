package com.cn.game.sdk2.ui.helper

import android.view.ViewTreeObserver.OnWindowAttachListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.cn.game.sdk2.ui.view.game.Fast3Toast
import com.xcjh.base_lib2.ModuleInitializer

/**
 * 快3Toast辅助类
 */
object Fast3ToastHelper {
    private var _instance: Fast3Toast? = null
    private var host:ConstraintLayout? = null

    fun attachToHost(anchorView:ConstraintLayout){
        this.host = anchorView
    }

    private fun attachToHost(){
        assert(host != null)
        val host = this.host!!
        val context = ModuleInitializer.application
        _instance = Fast3Toast(context, host)
        host.viewTreeObserver.addOnWindowAttachListener(object :OnWindowAttachListener {
            override fun onWindowAttached() {
            }
            override fun onWindowDetached() {
                host.viewTreeObserver.removeOnWindowAttachListener(this)
                this@Fast3ToastHelper.destroy()
            }
        })
    }

    fun showToastNormal(msg: CharSequence, duration: Long = 2_000,canReplace:Boolean = true) {
        if(_instance == null) attachToHost()
        _instance?.showToastNormal(msg,duration,canReplace)
    }

    fun dismissToast() {
        _instance?.dismissToast()
    }

    fun destroy(){
        _instance = null
        host = null
    }
}