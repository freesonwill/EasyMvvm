package arch.cayenne.module.bet

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.module.bet.repo.BalanceRepository
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.bet.repo.BetResultRepository
import arch.cayenne.module.bet.repo.SingleBetRepository
import arch.cayenne.module.bet.repo.ComboBetRepository
import arch.cayenne.module.bet.repo.FloatingButtonRepository
import arch.cayenne.module.bet.repo.ReserveRepository
import arch.cayenne.module.bet.viewmodel.ComboBetViewModel
import arch.cayenne.module.bet.viewmodel.ComboBetMoneyKeyboardDialogViewModel
import arch.cayenne.module.bet.viewmodel.SingleBetViewModel
import arch.cayenne.module.bet.viewmodel.ReserveDialogViewModel
import arch.cayenne.module.bet.viewmodel.FloatingButtonViewModel
import arch.cayenne.module.bet.viewmodel.ReserveViewModel
import arch.cayenne.module.bet.viewmodel.BetResultViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class BetModuleInitializer: DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val managerModule = module {
        factory {
            CoroutineScope(Dispatchers.IO)
        }
        factoryOf(::BettingRemoteManager)
    }

    private val viewModules = module {
        viewModelOf(::FloatingButtonViewModel)
        viewModelOf(::SingleBetViewModel)
        viewModelOf(::ReserveDialogViewModel)
        viewModelOf(::ComboBetViewModel)
        viewModelOf(::BetResultViewModel)
        viewModelOf(::ComboBetMoneyKeyboardDialogViewModel)
        viewModelOf(::ReserveViewModel)
    }
    private val repoModules = module {
        factoryOf(::FloatingButtonRepository)
        factoryOf(::SingleBetRepository)
        factoryOf(::ComboBetRepository)
        factoryOf(::ReserveRepository)
        factoryOf(::BetResultRepository)
        factoryOf(::BetRepository)
        factoryOf(::BalanceRepository)
    }
    private val moduleList:List<Module> = listOf(managerModule, viewModules, repoModules)
}