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
import arch.cayenne.lib.common.CommonModuleInitializer
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.helper.TimesExitOnBackPressedHelper
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.DatabaseModuleInitializer
import arch.cayenne.lib.http.HttpModuleInitializer
import arch.cayenne.lib.http._interface.IApi
import arch.cayenne.lib.websocket.SocketModuleInitializer
import arch.cayenne.module.home.HomeModuleInitializer
import com.walisport.app.data.PreloadDataModel
import com.walisport.app.data.repo.MainRepository
import com.walisport.app.data.repo.ModuleRepository
import com.walisport.app.data.repo.SplashRepository
import com.walisport.app.ui.viewmodel.MainViewModel
import com.walisport.app.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
                if(activity is FragmentActivity){
                    TimesExitOnBackPressedHelper(activity as FragmentActivity,2){ remain, times->
                        activity.showToast(R.string.more_taps_to_exit.getString())
                        //activity.showToast(R.string.more_taps_to_exit2.getString(remain))
                    }.attach()
                }
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
        val moduleRepository = GlobalContext.get().get<ModuleRepository>()
        moduleRepository.preLoadHome()
        moduleRepository.startSocket()
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return super.dependencies() + listOf(
            SocketModuleInitializer::class.java,
            HttpModuleInitializer::class.java,
            DatabaseModuleInitializer::class.java,
            CommonModuleInitializer::class.java,
            HomeModuleInitializer::class.java,
        )
    }

    private val viewModules = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::SplashViewModel)
    }
    private val repoModules = module {
        factory { CoroutineScope(Dispatchers.IO) }
        single { MutableStateFlow(PreloadEnum.INIT) }
        factory { ModuleRepository(get(), get(), get(named("preLoadHome")), get(), get()) }
        factory { (scope: CoroutineScope) -> MainRepository(scope, get(), get(), get(), get(), get()) }
        factory { (scope: CoroutineScope) -> SplashRepository(scope, get(), get()) }
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}

interface IPreLoadHomeApi : IApi {
    @POST("sport_server/game/firstLoad")
    suspend fun postPreLoad(@Body params: Map<String, String>): Response<PreloadDataModel>
}


