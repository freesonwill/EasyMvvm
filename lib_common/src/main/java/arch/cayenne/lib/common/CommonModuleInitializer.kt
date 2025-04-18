package arch.cayenne.lib.common

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.common.data.UserDataManager
import com.tencent.mmkv.MMKV
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class CommonModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        MMKV.initialize(context)
        loadKoinModules(moduleList)
        return TAG
    }

    private val moduleList: List<Module> = listOf(module {
        factory { UserDataManager() }
    })
}
