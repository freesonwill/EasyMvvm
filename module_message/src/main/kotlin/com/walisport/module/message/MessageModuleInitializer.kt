package com.walisport.module.message

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.message.data.MessageMainRepository
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import com.walisport.module.message.ui.viewmodel.TodayMatchViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class MessageModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        viewModelOf(::MessageMainViewModel)
        viewModelOf(::TodayMatchViewModel)
    }

    private val repoModules = module {
        factoryOf(::MessageMainRepository)

    }

    private val managerModule = module {
        factoryOf(::MessageRemoteManager)

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}