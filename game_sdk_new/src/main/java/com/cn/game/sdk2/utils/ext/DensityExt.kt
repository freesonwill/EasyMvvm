package com.cn.game.sdk2.utils.ext

import android.content.Context
import com.xcjh.base_lib2.ModuleInitializer

object DensityExt {
    fun Context.dp2px(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
    inline val Int.dp2px
        get() = run {
            val context = ModuleInitializer.application
            val scale = context.resources.displayMetrics.density
            val dp = this
            (dp * scale + 0.5).toInt()
        }

    inline val Int.px2dp
        get() = run {
            val context = ModuleInitializer.application
            val scale = context.resources.displayMetrics.density
            val v = this
            (v / scale + 0.5f).toInt()
        }
    inline val Float.dp2px
        get() = run {
            val context = ModuleInitializer.application
            val scale = context.resources.displayMetrics.density
            val dp = this
            (dp * scale + 0.5f).toInt()
        }

    inline val Float.px2dp
        get() = run {
            val context = ModuleInitializer.application
            val scale = context.resources.displayMetrics.density
            val v = this
            (v / scale + 0.5f).toInt()
        }
}