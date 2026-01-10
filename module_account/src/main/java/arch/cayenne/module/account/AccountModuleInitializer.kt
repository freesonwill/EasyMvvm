package arch.cayenne.module.account

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.account.data.repo.AccountLoginRepository
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * @author: ricky.chang
 * @date: 2025/6/13 下午3:40
 * @description:
 */
class AccountModuleInitializer : DefaultInitializer<Unit> {
    private val TAG = "AccountModuleInitializer"
    override fun create(context: Context) {
        "$TAG create ....".logd(TAG)
        loadKoinModules(moduleList)
    }

    private val viewModules = module {
        includes(defaultModule)
    }

    private val repoModules = module {
        factory {
            CoroutineScope(Dispatchers.IO)
        }
        factory {
            PersonalInfoRepository(
                get(), get(), get(), get(),
                get(named("3n1_http")),
            )
        }

        factory {
            AccountLoginRepository(
                get(), get(), get(), get(),
                get(named("3n1_http")),
            )
        }
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}