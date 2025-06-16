package arch.cayenne.module.account

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.core.module.Module
import com.ibm.icu.text.Transliterator
/**
 * @author: ricky.chang
 * @date: 2025/6/13 下午3:40
 * @description:
 */
class AccountModuleInitializer: DefaultInitializer<Unit> {
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

    private val repoModules = module {
        factory {
            CoroutineScope(Dispatchers.IO)
        }
        factory { PersonalInfoRepository(get(), get(), get()) }
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}