package arch.cayenne.lib.chatwebsocket

import android.app.Application
import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.chatwebsocket.data.ChatISecurity
import arch.cayenne.lib.chatwebsocket.data.ChatISocket
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import java.lang.ref.WeakReference

class ChatSocketModuleInitializer : DefaultInitializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        "$TAG init....~~~~".logd(TAG)
        loadKoinModules(moduleList)
        return TAG
    }

    private val socketModules = module {
        factory<ChatISocket<*, *, *>> { ChatSocketClientService(context = WeakReference(androidContext() as Application), get()) }
        factory<ChatISecurity<*, *, *>> { ChatNativeLib() }
        single { ChatWebSocketManager(get()) }
    }

    private val moduleList:List<Module> = listOf(socketModules)
}
