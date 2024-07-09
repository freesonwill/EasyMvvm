package com.cn.game.sdk2.ui.helper

import android.view.ViewTreeObserver.OnWindowAttachListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.cn.game.sdk2.ui.view.Fast3Toast
import com.xcjh.base_lib2.ModuleInitializer

/**
 * 快3Toast辅助类
 */
object Fast3ToastHelper {
    private var _instance:Fast3Toast? = null
    private lateinit var anchorView:ConstraintLayout

    fun init(anchorView:ConstraintLayout){
        this.anchorView = anchorView
    }

    private fun init(){
        assert(::anchorView.isInitialized)
        val context = ModuleInitializer.application
        _instance = Fast3Toast(context, anchorView)
        this.anchorView.viewTreeObserver.addOnWindowAttachListener(object :OnWindowAttachListener {
            override fun onWindowAttached() {
            }
            override fun onWindowDetached() {
                anchorView.viewTreeObserver.removeOnWindowAttachListener(this)
                this@Fast3ToastHelper.destroy()
            }
        })
    }

    fun showToastNormal(msg: CharSequence, duration: Long = 2_000) {
        if(_instance == null) init()
        _instance?.showToastNormal(msg,duration)
    }

    fun dismissToast() {
        _instance?.dismissToast()
    }

    fun destroy(){
        _instance = null
    }
}