package arch.cayenne.lib.database

import android.content.Context
import androidx.startup.Initializer
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
        return emptyList()
    }

    private val daoModule = module {
        factory { get<GameDatabase>().betDao() }
    }

    private val moduleList: List<Module> = listOf(module {
        single { GameDatabase.invoke(context = get()) }
    }, daoModule)
}