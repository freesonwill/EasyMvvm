package com.walisport.module.topup

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.topup.data.TopUpMainRepository
import com.walisport.module.topup.ui.viewmodel.TopUpMainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class TopUpModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        viewModelOf(::TopUpMainViewModel)
    }

    private val repoModules = module {
        factoryOf(::TopUpMainRepository)

    }

    private val managerModule = module {
        factoryOf(::TopUpRemoteManager)

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}