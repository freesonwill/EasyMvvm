package arch.cayenne.lib.websocket

import android.app.Application
import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.websocket.chat.ChatSocketClientService
import arch.cayenne.lib.websocket.chat.ChatWebSocketManager
import arch.cayenne.lib.websocket.data.ThreadSafeAutoIncrementID
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
        "$TAG init....~~~~".logd(TAG)
        loadKoinModules(moduleList)
        return TAG
    }

    private val socketModules = module {
        factory<ISocket<*, *, *>> { SocketClientService(context = WeakReference(androidContext() as Application), get()) }
        factory<ISecurity<*, *, *>> { NativeLib() }
        factory<ChatSocketClientService> { ChatSocketClientService(context = WeakReference(androidContext() as Application), get()) }
        single { WebSocketManager(get()) }
        single { ChatWebSocketManager(get()) }
        single { ThreadSafeAutoIncrementID(max = 0xFF) }
    }

    private val moduleList:List<Module> = listOf(socketModules)
}
