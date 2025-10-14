package com.walisport.module.gamedetail

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class GameDetailModuleInitializer: DefaultInitializer<Unit> {

    override fun create(context: Context) {
        loadKoinModules(moduleList)
    }

    private val viewModules = module {
        includes(defaultModule)
    }

    private val repoModules = module {

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}