package arch.cayenne.lib.skin

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.data.DefaultInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class SkinModuleInitializer : DefaultInitializer<String> {
    private val TAG: String = this.javaClass.simpleName
    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val socketModules = module {
        single<SportSkinManager> { SportSkinManager() }
    }
    private val moduleList: List<Module> = listOf(socketModules)

}