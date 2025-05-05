package com.walisport.module.setting

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.setting.data.SettingRepository
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class SettingModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        includes(defaultModule)
    }
    private val repoModules = module {
        factory { (scope: CoroutineScope) -> SettingRepository(scope) }
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}