package com.walisport.module.feedback

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class FeedbackModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        includes(autoViewModels)
    }

    private val repoModules = module {

    }

    private val managerModule = module {

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}