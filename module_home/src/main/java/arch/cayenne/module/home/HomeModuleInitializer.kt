package arch.cayenne.module.home

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.module.home.data.repo.ChampionRepository
import arch.cayenne.module.home.data.repo.CollectListRepository
import arch.cayenne.module.home.data.repo.HomeRepository
import arch.cayenne.module.home.data.repo.MatchListRepository
import arch.cayenne.module.home.data.repo.TournamentListRepository
import com.ibm.icu.text.Transliterator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

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
        preloadTransliterator()
    }

    private fun preloadTransliterator() {
        CoroutineScope(Dispatchers.IO).launch {
            // 強制觸發初始化（背景執行）
            getKoin().get<Transliterator>()
        }
    }

    private val viewModules = module {
        includes(defaultModule)
        single(createdAtStart = true) { Transliterator.getInstance("Han-Latin/Names; Latin-ASCII") }
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
        factory { TournamentListRepository(get(), get(), get<GameDatabase>().tournamentDao()) }
        factory { CollectListRepository(get(), get(), get<GameDatabase>().betDao(), get<GameDatabase>().matchDao()) }
        factory { MatchListRepository(get(), get(), get<GameDatabase>().betDao(), get<GameDatabase>().matchDao()) }
    }
    private val moduleList: List<Module> = listOf(viewModules, daoModule, repoModules)
}