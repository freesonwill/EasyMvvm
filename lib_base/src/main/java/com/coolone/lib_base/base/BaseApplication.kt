package com.coolone.lib_base.base

import android.app.Application
import com.blankj.utilcode.util.LogUtils

class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        LogUtils.d("ModuleInitializer--->BaseApplication")
    }

}