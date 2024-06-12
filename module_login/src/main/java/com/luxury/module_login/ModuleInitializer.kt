package com.luxury.module_login

import android.content.Context
import android.util.Log
import androidx.startup.Initializer

/**
 * @Description:   模块初始化
 * @Author: baoyuedong
 * @Date: 2024/6/12 14:53
 */
class ModuleInitializer : Initializer<String> {
    companion object {
        const val TAG = "module_login"
    }

    override fun create(context: Context): String {
        Log.d(TAG, "ModuleInitializer--->create")
        return "module_login"
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        //不依赖启动模块
        return emptyList()
    }

}