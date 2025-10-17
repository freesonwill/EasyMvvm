package com.walisport.module.me

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.me.data.MeRepository
import com.walisport.module.me.ui.viewmodel.FeaturesViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class MeModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        includes(defaultModule)
        viewModelOf(::FeaturesViewModel)
    }
    private val repoModules = module {
        factoryOf(::MeRepository)
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}