package arch.cayenne.module.home

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.home.repository.HomeRepository
import arch.cayenne.module.home.viewmodel.DrawerContentViewModel
import arch.cayenne.module.home.viewmodel.HomeViewModel
import arch.cayenne.module.home.viewmodel.MatchListViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
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
        viewModel { HomeViewModel() }
        viewModel { MatchListViewModel() }
        viewModel { DrawerContentViewModel() }
    }
    private val daoModule = module {
//        factory { get<GameDatabase>().sportDao() }
    }
    private val repoModules = module {
        factory { (scope: CoroutineScope) -> HomeRepository(scope, get(), get()) }
    }
    private val moduleList: List<Module> = listOf(viewModules, daoModule, repoModules)
}