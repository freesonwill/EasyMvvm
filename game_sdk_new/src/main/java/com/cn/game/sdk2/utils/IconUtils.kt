package com.cn.game.sdk2.utils

import android.content.Context
import com.xcjh.base_lib2.ModuleInitializer

object IconUtils {

    /***
     * @key String ex. game_sdk_icon_select
     * @value Int ex. R.mipmap.ic_launcher
     */
    private val iconMap = mutableMapOf<String, Int?>()
    private val context: Context
        get() = ModuleInitializer.application

    fun getIcon(name: String): Int {
        var id = iconMap[name]
        if (id == null) {
            id = context.resources.getIdentifier(
                name,
                "mipmap",
                context.packageName
            )
            if (id != 0 && id != -1) {
                iconMap[name] = id
            }
        }
        return id
    }
}