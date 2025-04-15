package arch.cayenne.lib.common

import android.content.Context
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV
import arch.cayenne.lib.base.ApplicationModuleInitializer
import arch.cayenne.lib.common.data.UserDataManager
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class CommonModuleInitializer : Initializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        MMKV.initialize(context)
        loadKoinModules(moduleList)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java)
    }

    private val moduleList: List<Module> = listOf(module {
        factory { UserDataManager() }

    })
}
