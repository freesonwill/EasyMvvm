package com.walisport.lib_socket

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib_base.utils.LogUtilsExt.logd
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import java.lang.ref.WeakReference

class SocketModuleInitializer : Initializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        "$TAG init....~~~~".logd(TAG)
        loadKoinModules(moduleList)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

    private val socketModules = module {
        factory<ISocket<*,*,*>> { SocketClientService(context = WeakReference(androidContext()), get()) }
        factory<ISecurity<*, *, *>> { NativeLib() }
        single { WebSocketManager(get()) }
    }

    private val moduleList:List<Module> = listOf(socketModules)
}
