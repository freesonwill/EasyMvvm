package com.walisport.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.startup.Initializer
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.ui.BaseActivity
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.chatwebsocket.ChatSocketModuleInitializer
import arch.cayenne.lib.common.CommonModuleInitializer
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.helper.TimesExitOnBackPressedHelper
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.websocket.SocketModuleInitializer
import com.walisport.app.data.repo.MainRepository
import com.walisport.app.data.repo.SplashRepository
import com.walisport.app.ui.viewmodel.MainViewModel
import com.walisport.app.ui.viewmodel.SplashViewModel
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
class ModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName
    private var activityCount = 0
    private val activityLifecycleCallback by lazy {
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityCount++
                "Activity $activity has created. Count: $activityCount".logi(TAG)
                TimesExitOnBackPressedHelper(activity as FragmentActivity,2){ remain, times->
                    activity.showToast(R.string.more_taps_to_exit.getString())
                    //activity.showToast(R.string.more_taps_to_exit2.getString(remain))
                }.attach()
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {
                "Activity $activity has pause. Count: $activityCount".logi(TAG)
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                activityCount--
                "Activity $activity has destroyed. Count: $activityCount".logi(TAG)
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
        return super.dependencies() + listOf(
            SocketModuleInitializer::class.java,
            CommonModuleInitializer::class.java,
            HomeModuleInitializer::class.java,
            ChatSocketModuleInitializer::class.java,
        )
    }

    private val viewModules = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::SplashViewModel)
    }
    private val repoModules = module {
        factory { (scope: CoroutineScope) -> MainRepository(scope, get()) }
        factory { (scope: CoroutineScope) -> SplashRepository(scope, get(), get()) }
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}