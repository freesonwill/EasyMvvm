package arch.cayenne.lib.socket

import android.app.Application
import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.socket.data.ISecurity
import arch.cayenne.lib.socket.data.ISocket
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import java.lang.ref.WeakReference

class SocketModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        "$TAG init....~~~~".logd(TAG)
        loadKoinModules(moduleList)
        return TAG
    }

    private val socketModules = module {
        factory<ISocket<*, *, *>> { SocketClientService(context = WeakReference(androidContext() as Application), get()) }
        factory<ISecurity<*, *, *>> { NativeLib() }
        single { WebSocketManager(get()) }
    }

    private val moduleList:List<Module> = listOf(socketModules)
}
