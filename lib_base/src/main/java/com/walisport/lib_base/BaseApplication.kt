package com.walisport.lib_base

import android.app.Application
import com.walisport.lib_base.utils.LogUtils
import com.walisport.lib_base.utils.LogUtilsExt
import com.walisport.lib_base.utils.LogUtilsExt.logd
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * @author: zhangsan
 * @date: 2025/3/14 14:01
 * @description:
 */
class BaseApplication : Application() {
    private val TAG = this.javaClass.simpleName

    override fun onCreate() {
        super.onCreate()
        "$TAG init....".logd(TAG)

        //LogUtils init
        LogUtils.getConfig()
            .setConsoleFilter(if (BuildConfig.DEBUG) LogUtils.V else LogUtils.V)
            .setTagPrefix(LogUtilsExt.TAG)
            .setBorderSwitch(false)
            .setLog2FileSwitch(true)
            .setSaveDays(7)
            .setLogHeadSwitch(false)
            .setSingleTagSwitch(false)

        //Koin init
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(moduleList)
        }


    }


}