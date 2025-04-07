package com.walisport.app

import android.app.Application
import com.walisport.lib.common.utils.UserSetting

class ProxyApplication : Application() {

    companion object {
        lateinit var instance :ProxyApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        UserSetting.getInstance().initializeMMKV(this)
    }
}