package com.walisport.module.home

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.utils.LogUtilsExt.logd
import com.walisport.module.home.repository.HomeRepository
import com.walisport.module.home.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * @author: zhangsan
 * @date: 2025/3/26 10:27
 * @description:
 */
class HomeModuleInitializer: Initializer<Unit> {
    private val TAG = "ModuleInitializer"
    override fun create(context: Context) {
        "$TAG create ....".logd(TAG)
        loadKoinModules(moduleList)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {

       return emptyList()
    }

    private val viewModules = module {
        viewModel { HomeViewModel() }
    }
    private val daoModule = module {
//        factory { get<GameDatabase>().sportDao() }
    }
    private val repoModules = module {
        factory { (scope: CoroutineScope) -> HomeRepository(scope, get(), get()) }
    }
    private val moduleList: List<Module> = listOf(viewModules, daoModule, repoModules)
}