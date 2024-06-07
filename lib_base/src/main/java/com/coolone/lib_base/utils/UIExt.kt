package com.coolone.lib_base.utils

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity


/**
 * UI相关扩展函数，比如Activity,Fragment,View等
 */
object UIExt {

    /**
     * Activity的返回扩展函数
     */
    @JvmStatic()
    fun AppCompatActivity.onBackPressed(enabled: Boolean, callback: () -> Unit) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                callback()
            }
        })
    }
}


