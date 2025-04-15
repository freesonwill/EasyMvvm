package com.walisport.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.startup.Initializer
import arch.cayenne.lib.base.ApplicationModuleInitializer
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.LogUtilsExt.logi
import arch.cayenne.lib.common.CommonModuleInitializer
import arch.cayenne.lib.socket.SocketModuleInitializer
import com.walisport.app.data.AppNavViewModel
import com.walisport.app.data.MainRepository
import com.walisport.app.data.SplashRepository
import com.walisport.app.data.MainViewModel
import com.walisport.app.data.SplashViewModel
import arch.cayenne.module.home.HomeModuleInitializer
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * @author: zhangsan
 * @date: 2025/3/14 17:17
 * @description:
 */
class ModuleInitializer : Initializer<String> {
    private val TAG = "ModuleInitializer"
    private var activityCount = 0
    private val activityLifecycleCallback by lazy {
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityCount++
                "Activity $activity has created. Count: $activityCount".logi(ModuleInitializer::class.java.simpleName)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {
                "Activity $activity has pause. Count: $activityCount".logi(ModuleInitializer::class.java.simpleName)
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                activityCount--
                "Activity $activity has destroyed. Count: $activityCount".logi(ModuleInitializer::class.java.simpleName)
                if (activityCount == 0 && activity is BaseActivity<*,*>) {
                    activity.reset()
                }
            }
        }
    }

    override fun create(context: Context): String {
        "$TAG create ....".logd(TAG)
        loadKoinModules(moduleList)
        (context as Application).registerActivityLifecycleCallbacks(activityLifecycleCallback)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java, SocketModuleInitializer::class.java, CommonModuleInitializer::class.java, HomeModuleInitializer::class.java)
    }

    private val viewModules = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::SplashViewModel)
        viewModelOf(::AppNavViewModel)
    }
    private val repoModules = module {
        factory { (scope: CoroutineScope) -> MainRepository(scope, get()) }
        factory { (scope: CoroutineScope) -> SplashRepository(scope, get(), get()) }
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}

