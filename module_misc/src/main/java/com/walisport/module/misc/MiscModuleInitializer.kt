package com.walisport.module.misc

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.misc.data.MiscRepository
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class MiscModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        includes(defaultModule)
    }
    private val repoModules = module {
        factoryOf(::MiscRepository)

    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}