package arch.cayenne.module.bet

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.ApplicationModuleInitializer
import arch.cayenne.module.bet.repo.SingleBetRepository
import arch.cayenne.module.bet.repo.ComboBetRepository
import arch.cayenne.module.bet.repo.FloatingButtonRepository
import arch.cayenne.module.bet.repo.ReserveRepository
import arch.cayenne.module.bet.viewmodel.ComboBetResultViewModel
import arch.cayenne.module.bet.viewmodel.ComboBetViewModel
import arch.cayenne.module.bet.viewmodel.ComboBetMoneyKeyboardDialogViewModel
import arch.cayenne.module.bet.viewmodel.SingleBetViewModel
import arch.cayenne.module.bet.viewmodel.ReserveDialogViewModel
import arch.cayenne.module.bet.viewmodel.FloatingButtonViewModel
import arch.cayenne.module.bet.viewmodel.ReserveViewModel
import arch.cayenne.module.bet.viewmodel.SingleBetResultViewModel
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
        viewModelOf(::ComboBetMoneyKeyboardDialogViewModel)
        viewModelOf(::ReserveViewModel)
    }
    private val repoModules = module {
        factoryOf(::FloatingButtonRepository)
        factoryOf(::SingleBetRepository)
        factoryOf(::ComboBetRepository)
        factoryOf(::ReserveRepository)
    }
    private val moduleList:List<Module> = listOf(viewModules,repoModules)
}