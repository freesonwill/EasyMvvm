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
        factory { get<GameDatabase>().matchDao() }
        factory { get<GameDatabase>().liveVideoDao() }
        factory { get<GameDatabase>().marketTypeDao() }
        factory { get<GameDatabase>().liveMatchDao() }
    }

    private val moduleList: List<Module> = listOf(module {
        single { GameDatabase.invoke(context = get()) }
    }, daoModule)
}