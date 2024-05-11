package com.xcjh.base_lib

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.alibaba.android.arouter.launcher.ARouter
import com.xcjh.base_lib.manager.KtxActivityManger
import com.xcjh.base_lib.utils.loge
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.utils.AutoSizeLog
import me.jessyan.autosize.utils.AutoSizeLog.isDebug
val appContext: App by lazy { App.app }
open class App : Application(), Application.ActivityLifecycleCallbacks, ViewModelStoreOwner {
    public lateinit var mAppViewModelStore: ViewModelStore
    private var mFactory: ViewModelProvider.Factory? = null

    companion object {
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        app = this

        if (isDebug()) {
            // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog() // 打印日志
            ARouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
       // ARouter.init(this)
        mAppViewModelStore = ViewModelStore()
        registerActivityLifecycleCallbacks(this)


    }


    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
    }

    /**
     * 获取一个全局的ViewModel
     */
    fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, this.getAppFactory())
    }

    private fun getAppFactory(): ViewModelProvider.Factory {
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mFactory as ViewModelProvider.Factory
    }

    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        KtxActivityManger.pushActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
       // "onActivityResumed".loge(activity.componentName.toString()+"==="+activity.localClassName)
    }

    override fun onActivityPaused(activity: Activity) {
       // "onActivityPaused".loge(activity.componentName.toString()+"==="+activity.localClassName)
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
       // "onActivityDestroyed".loge("===")
        KtxActivityManger.popActivity(activity)
    }

}