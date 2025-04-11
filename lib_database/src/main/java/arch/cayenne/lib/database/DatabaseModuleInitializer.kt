package arch.cayenne.lib.database

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.ApplicationModuleInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class DatabaseModuleInitializer: Initializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java)
    }

    private val daoModule = module {
        factory { get<GameDatabase>().betDao() }
    }

    private val moduleList: List<Module> = listOf(module {
        single { GameDatabase.invoke(context = get()) }
    }, daoModule)
}