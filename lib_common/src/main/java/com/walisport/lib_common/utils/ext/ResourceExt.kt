package com.walisport.lib_common.utils.ext

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.R
import org.koin.java.KoinJavaComponent.getKoin

/**
 * @author: zhangsan
 * @date: 2025/3/31 16:44
 * @description: 资源扩展
 */
object ResourceExt {

    /***
     *  無Context狀態下取得String
     */
    fun @receiver:StringRes Int.getString(vararg formatArgs: Any): String {
        return getKoin().get<Application>().getString(this, *formatArgs)
    }


    /***
     *  获取Color
     */
    fun @receiver:ColorRes Int.getColor(): Int {
        return ContextCompat.getColor(getKoin().get<Application>(), this)
    }

    /**
     * 获取Drawable
     * @return
     */
    fun @receiver:DrawableRes Int.getDrawable(): Drawable {
        return ContextCompat.getDrawable(getKoin().get<Application>(), this)!!
    }

}