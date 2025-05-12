package arch.cayenne.lib.base

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.data.repository.EmptyRepository
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * @author: zhangsan
 * @date: 2025/3/14 18:20
 * @description:
 */
class ApplicationModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        //LogUtils init
        LogUtils.getConfig()
            .setConsoleFilter(if (BuildConfig.DEBUG) LogUtils.V else LogUtils.V)
            .setTagPrefix(BuildConfig.TAG_DEFAULT)
            .setBorderSwitch(false)
            .setLog2FileSwitch(true)
            .setStackOffset(3)
            .setSaveDays(7)
            .setLogHeadSwitch(false)
            .setSingleTagSwitch(false)

        //Koin init
        startKoin {
            androidLogger()
            androidContext(context)
            modules(moduleList)
        }
        "$TAG init....".logd(TAG)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        super.dependencies()
        return emptyList()
    }

    private val viewModules = module {
        includes(defaultModule)
    }

    private val repoModules = module {
        singleOf(::EmptyRepository)
    }
    private val moduleList:List<Module> = listOf(viewModules,repoModules)
}
