package arch.cayenne.lib.base.ui._interface

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @date: 2025/8/18 19:54
 * @description: ActivityLifecycleCallbacks的简化接口实现
 */
interface SimpleActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}