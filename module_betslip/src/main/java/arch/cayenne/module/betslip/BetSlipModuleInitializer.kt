package arch.cayenne.module.betslip

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import arch.cayenne.module.betslip.data.repo.BetSlipRepository
import arch.cayenne.module.betslip.data.repo.HomeBetSlipRepository
import arch.cayenne.module.betslip.data.repo.SportPickerRepository
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipFilterViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipModifyOddsViewModel
import arch.cayenne.module.betslip.ui.viewmodel.BetSlipViewModel
import arch.cayenne.module.betslip.ui.viewmodel.DatePickerViewModel
import arch.cayenne.module.betslip.ui.viewmodel.EarlySettledKeyboardViewModel
import arch.cayenne.module.betslip.ui.viewmodel.HomeBetSlipViewModel
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import arch.cayenne.module.betslip.ui.viewmodel.TimePickerViewModel

import org.koin.dsl.module



class BetSlipModuleInitializer: DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val managerModule = module {
        factory {
            CoroutineScope(Dispatchers.IO)
        }
        factoryOf(::BetSlipRemoteManager)
    }

    private val viewModules = module {
        includes(defaultModule)
        viewModelOf(::BetSlipViewModel)
        viewModelOf(::BetSlipModifyOddsViewModel)
        viewModelOf(::EarlySettledKeyboardViewModel)
        viewModelOf(::DatePickerViewModel)
        viewModelOf(::TimePickerViewModel)
        viewModelOf(::HomeBetSlipViewModel)
        viewModelOf(::SportPickerViewModel)
        viewModelOf(::BetSlipFilterViewModel)
    }

    private val repoModules = module {
        factoryOf(::HomeBetSlipRepository)
        factoryOf(::BetSlipRepository)
        factoryOf(::SportPickerRepository)
    }

    private val moduleList:List<Module> = listOf(managerModule, viewModules, repoModules)
}