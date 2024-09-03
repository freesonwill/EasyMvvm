package com.cn.game.sdk2.utils

import android.content.Context
import com.xcjh.base_lib2.ModuleInitializer

object IconUtils {

    /***
     * @key String ex. "R.mipmap.ic_launcher"
     * @value Int ex. R.mipmap.ic_launcher
     */
    private val iconMap = mutableMapOf<String, Int?>()
    private val context: Context
        get() = ModuleInitializer.application

    fun getIcon(key: String): Int {
        var id = iconMap[key]
        if (id == null) {
            id = context.resources.getIdentifier(
                key,
                "mipmap",
                context.packageName
            )
            if (id != 0 && id != -1) {
                iconMap[key] = id
            }
        }
        return id
    }
}