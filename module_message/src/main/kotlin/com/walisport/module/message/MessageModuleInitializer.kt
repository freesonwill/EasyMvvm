package com.walisport.module.message

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.database.GameDatabase
import com.walisport.module.message.data.MessageMainRepository
import com.walisport.module.message.ui.viewmodel.MessageMainViewModel
import com.walisport.module.message.ui.viewmodel.TodayMatchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        factory {
            CoroutineScope(Dispatchers.IO)
        }
        factory { MessageMainRepository(get(), get<GameDatabase>().msgDao()) }
    }

    private val managerModule = module {
        factoryOf(::MessageRemoteManager)

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}