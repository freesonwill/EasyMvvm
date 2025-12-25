package com.walisport.module.business.common

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class BusinessCommonModuleInitializer: DefaultInitializer<Unit> {

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