package com.walisport.module.popup.slot

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.popup.slot.data.PopupSlotRepository
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class PopUpSlotModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        includes(defaultModule)
    }
    private val repoModules = module {
        singleOf(::PopupSlotRepository)
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}