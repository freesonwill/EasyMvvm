package com.walisport.app

import android.content.Context
import androidx.startup.Initializer
import com.walisport.app.data.MainRepository
import com.walisport.app.data.MainViewModel
import com.walisport.lib_base.ApplicationModuleInitializer
import com.walisport.lib_base.utils.LogUtilsExt.logd
import org.koin.androidx.viewmodel.dsl.viewModel
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
        return listOf(ApplicationModuleInitializer::class.java)
    }

    private val viewModules = module {
        viewModel { MainViewModel(get()) }
    }
    private val repoModules = module {
        single {
            MainRepository()
        }
    }
    private val moduleList:List<Module> = listOf(viewModules,repoModules)
}

