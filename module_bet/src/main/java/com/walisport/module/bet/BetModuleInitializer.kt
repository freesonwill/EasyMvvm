package com.walisport.module.bet

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib.base.ApplicationModuleInitializer
import com.walisport.module.bet.repo.BetSheetRepository
import com.walisport.module.bet.repo.FloatingButtonRepository
import com.walisport.module.bet.viewmodel.BetSheetViewModel
import com.walisport.module_bet.viewmodel.FloatingButtonViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class BetModuleInitializer: Initializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java)
    }

    private val viewModules = module {
        viewModelOf(::FloatingButtonViewModel)
        viewModelOf(::BetSheetViewModel)
    }
    private val repoModules = module {
        factoryOf(::FloatingButtonRepository)
        factoryOf(::BetSheetRepository)
    }
    private val moduleList:List<Module> = listOf(viewModules,repoModules)
}