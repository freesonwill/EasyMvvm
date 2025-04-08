package com.walisport.app

import android.content.Context
import androidx.startup.Initializer
import com.walisport.app.data.AppNavViewModel
import com.walisport.app.data.MainRepository
import com.walisport.app.data.SplashRepository
import com.walisport.app.data.MainViewModel
import com.walisport.app.data.SplashViewModel
import com.walisport.lib.base.ApplicationModuleInitializer
import com.walisport.lib.base.utils.LogUtilsExt.logd
import com.walisport.lib.common.CommonModuleInitializer
import com.walisport.lib_socket.SocketModuleInitializer
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

    override fun create(context: Context): String {
        "$TAG create ....".logd(TAG)
        loadKoinModules(moduleList)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java, SocketModuleInitializer::class.java, CommonModuleInitializer::class.java)
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
    private val moduleList:List<Module> = listOf(viewModules,repoModules)
}

