package arch.cayenne.module.home

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.module.home.data.repo.ChampionRepository
import arch.cayenne.module.home.data.repo.HomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * @author: zhangsan
 * @date: 2025/3/26 10:27
 * @description:
 */
class HomeModuleInitializer: DefaultInitializer<Unit> {
    private val TAG = "ModuleInitializer"
    override fun create(context: Context) {
        "$TAG create ....".logd(TAG)
        loadKoinModules(moduleList)
    }

    private val viewModules = module {
        includes(defaultModule)
    }
    private val daoModule = module {
//        factory { get<GameDatabase>().sportDao() }
    }
    private val repoModules = module {
        factory {
            CoroutineScope(Dispatchers.IO)
        }
        factory { HomeRepository(get(), get(), get()) }
        factory { ChampionRepository(get(), get(), get<GameDatabase>().matchDao(), get<GameDatabase>().betDao()) }
    }
    private val moduleList: List<Module> = listOf(viewModules, daoModule, repoModules)
}