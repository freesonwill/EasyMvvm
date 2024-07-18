package com.xcjh.base_lib2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.cn.game.sdk2.BuildConfig
import com.xcjh.base_lib2.utils.LogUtils

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
        application = context as Application
        LogUtils.getConfig()
            .setLogSwitch(BuildConfig.DEBUG)
            .setBorderSwitch(false)
            .setLogHeadSwitch(false)
            .setSingleTagSwitch(false)
            //.setGlobalTag("game_sdk")
        LogUtils.dTag(TAG, "ModuleInitializer--->create")
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        //不依赖启动模块
        return emptyList()
    }

}