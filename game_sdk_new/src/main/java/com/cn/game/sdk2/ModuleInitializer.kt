package com.cn.game.sdk2

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.startup.Initializer
import com.cn.game.sdk2.utils.GamePartyLibraryInitializer

/**
 * @Description:   模块初始化
 * @Author: brain
 * @Date: 2024/6/12 14:53
 */
class ModuleInitializer : Initializer<String> {
    companion object {
        const val TAG = "game_sdk"
    }

    override fun create(context: Context): String {
        val application = context as Application
        initGameSdk( application )
        return TAG
    }

    private fun initGameSdk(application:Application){
        val storeOwner = ViewModelStoreOwner {  ViewModelStore() }
//        GamePartyLibraryInitializer.initialize(storeOwner,application)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        //不依赖启动模块
        return emptyList()
    }

}