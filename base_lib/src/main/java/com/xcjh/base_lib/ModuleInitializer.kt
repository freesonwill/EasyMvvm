package com.xcjh.base_lib

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.startup.Initializer

/**
 * @Description:   模块初始化
 * @Author: brain
 * @Date: 2024/6/12 14:53
 */
class ModuleInitializer : Initializer<String> {
    companion object {
        const val TAG = "base_lib"
        lateinit var application:Application
    }

    override fun create(context: Context): String {
        Log.d(TAG, "ModuleInitializer--->create")
        application = context as Application
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        //不依赖启动模块
        return emptyList()
    }

}