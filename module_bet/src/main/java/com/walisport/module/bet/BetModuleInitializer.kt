package com.walisport.module.bet

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib.base.ApplicationModuleInitializer
import com.walisport.module.bet.repo.SingleBetRepository
import com.walisport.module.bet.repo.ComboBetRepository
import com.walisport.module.bet.repo.FloatingButtonRepository
import com.walisport.module.bet.viewmodel.ComboBetResultViewModel
import com.walisport.module.bet.viewmodel.ComboBetViewModel
import com.walisport.module.bet.viewmodel.SingleBetViewModel
import com.walisport.module.bet.viewmodel.ReserveDialogViewModel
import com.walisport.module.bet.viewmodel.FloatingButtonViewModel
import com.walisport.module.bet.viewmodel.SingleBetResultViewModel
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
        viewModelOf(::SingleBetViewModel)
        viewModelOf(::ReserveDialogViewModel)
        viewModelOf(::ComboBetViewModel)
        viewModelOf(::SingleBetResultViewModel)
        viewModelOf(::ComboBetResultViewModel)
    }
    private val repoModules = module {
        factoryOf(::FloatingButtonRepository)
        factoryOf(::SingleBetRepository)
        factoryOf(::ComboBetRepository)
    }
    private val moduleList:List<Module> = listOf(viewModules,repoModules)
}