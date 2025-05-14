package arch.cayenne.module.betslip

import arch.cayenne.module.bet.defaultModule
import org.koin.core.context.loadKoinModules
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.module.bet.repo.BetSheetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module


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
    }

    private val repoModules = module {
        factoryOf(::BetSheetRepository)
    }

    private val moduleList:List<Module> = listOf(managerModule, viewModules, repoModules)
}