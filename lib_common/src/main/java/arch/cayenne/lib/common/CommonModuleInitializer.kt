package arch.cayenne.lib.common

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.data.repo.CommonRepository
import arch.cayenne.lib.database.GameDatabase
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class CommonModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        MMKV.initialize(context)
        loadKoinModules(moduleList)
        return TAG
    }

    private val moduleList: List<Module> = listOf(module {
        factory { (scope: CoroutineScope) -> CommonRepository(scope, get(), get(), get(), get()) }
        factoryOf(::BalanceRepository)
        single { UserDataManager() }
    })
}
