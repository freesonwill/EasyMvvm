package arch.cayenne.lib.websocket

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.ChatSocketClientService
import arch.cayenne.lib.websocket.chat.ChatWebSocketManager
import arch.cayenne.lib.websocket.data.ISecurity
import arch.cayenne.lib.websocket.data.ISocket
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import java.lang.ref.WeakReference

class SocketModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        "$TAG create ....".logd(TAG)
        loadKoinModules(moduleList)
        return TAG
    }

    private val socketModules = module {
        factory { androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
        factory<ISocket<*, *, *>> { SocketClientService(context = WeakReference(androidContext() as Application), get()) }
        factory<ISecurity<*, *, *>> { NativeLib() }
        factory<ChatSocketClientService> { ChatSocketClientService(context = WeakReference(androidContext() as Application), get()) }
        single { WebSocketManager(get(), get()) }
        single { ChatWebSocketManager(get()) }
    }

    private val moduleList:List<Module> = listOf(socketModules)
}
