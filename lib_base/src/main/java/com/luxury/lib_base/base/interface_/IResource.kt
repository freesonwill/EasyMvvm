package com.luxury.lib_base.base.interface_

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.luxury.lib_base.ModuleInitializer.Companion.application

interface IResource {

    fun getString(@StringRes id: Int): String {
        return application.getString(id)
    }

    fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(application, id)
    }

    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(application, id)
    }
}