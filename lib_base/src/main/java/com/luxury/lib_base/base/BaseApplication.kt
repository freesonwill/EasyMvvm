package com.luxury.lib_base.base

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.luxury.lib_base.BuildConfig

/**
 * Description:
 * author       : brain
 * createTime   : 2024/6/12 11:58
 **/
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }
}