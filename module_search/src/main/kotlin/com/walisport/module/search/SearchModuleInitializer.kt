package com.walisport.module.search

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.search.viewmodel.SearchMainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class SearchModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        viewModelOf(::SearchMainViewModel)


    }
    private val repoModules = module {
    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}