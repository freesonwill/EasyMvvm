package com.walisport.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.walisport.lib.base.ui.BaseActivity
import com.walisport.lib.base.utils.LogUtilsExt.loge
import com.walisport.lib.common.utils.UserSetting
import org.koin.android.ext.android.getKoin

class ProxyApplication : Application() {

    companion object {
        lateinit var instance :ProxyApplication
    }
    var activityCount = 0
    override fun onCreate() {
        super.onCreate()
        instance = this
        UserSetting.getInstance().initializeMMKV(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityCount++
                "Activity $activity has created. Count: $activityCount".loge(ProxyApplication::class.java.simpleName)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {
                "Activity $activity has pause. Count: $activityCount".loge(ProxyApplication::class.java.simpleName)
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                activityCount--
                "Activity $activity has destroyed. Count: $activityCount".loge(ProxyApplication::class.java.simpleName)
                if (activityCount == 0 && activity is BaseActivity<*, *>) {
                    activity.reset()
                }
            }
        })
    }
}