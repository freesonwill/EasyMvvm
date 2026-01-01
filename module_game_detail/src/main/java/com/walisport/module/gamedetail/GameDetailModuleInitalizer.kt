package com.walisport.module.gamedetail

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.gamedetail.data.GameDetailRepository
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

class GameDetailModuleInitializer: DefaultInitializer<Unit> {

    override fun create(context: Context) {
        loadKoinModules(moduleList)
    }

    private val viewModules = module {
        includes(defaultModule)
    }

    private val repoModules = module {
        factory {
            GameDetailRepository(
                get() ,
                get() ,
                get(named("3n1")) ,
                get(named("mock")) ,
                get() ,
                get() ,
                get()
            )
        }
        
    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}