package com.walisport.module.setting

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.setting.data.OddsDisplayRepository
import com.walisport.module.setting.data.SettingRepository
import com.walisport.module.setting.ui.viewmodel.OddsDisplayViewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class SettingModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        includes(defaultModule)
        factoryOf(::OddsDisplayViewModel)
    }
    private val repoModules = module {
        factoryOf(::SettingRepository)
        factoryOf(::OddsDisplayRepository)
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}