package com.walisport.module.me

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.me.data.MeRepository
import com.walisport.module.me.ui.viewmodel.BottomViewModel
import com.walisport.module.me.ui.viewmodel.FeaturesViewModel
import com.walisport.module.me.ui.viewmodel.MeVIPInfoViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
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
        viewModelOf(::MeVIPInfoViewModel)
        viewModelOf(::BottomViewModel)
    }
    private val repoModules = module {
        factory {
            MeRepository(get(), get(), get(), get(), get(named("mock")))
        }
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}