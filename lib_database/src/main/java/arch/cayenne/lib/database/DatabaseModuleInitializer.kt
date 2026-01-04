package arch.cayenne.lib.database

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class DatabaseModuleInitializer: DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val daoModule = module {
        factory { get<GameDatabase>().infoDao() }
        factory { get<GameDatabase>().betDao() }
        factory { get<GameDatabase>().coinDao() }
        factory { get<GameDatabase>().matchDao() }
        factory { get<GameDatabase>().liveVideoDao() }
        factory { get<GameDatabase>().marketTypeDao() }
        factory { get<GameDatabase>().marketTypeMenuDao() }
        factory { get<GameDatabase>().liveMatchDao() }
        factory { get<GameDatabase>().sportDao() }
        factory { get<GameDatabase>().msgDao() }
        factory { get<GameDatabase>().betSlipReserveDao() }
        factory { get<GameDatabase>().betSlipOrderDao() }
        factory { get<GameDatabase>().collectListDao() }
        factory { get<GameDatabase>().chatConfigDao() }
        factory { get<GameDatabase>().userDataDao() }
        factory { get<GameDatabase>().currencyConfigDao() }
        factory { get<GameDatabase>().dailyBetMatchDataDao()}
    }

    private val moduleList: List<Module> = listOf(module {
        single { GameDatabase.invoke(context = get()) }
    }, daoModule)
}