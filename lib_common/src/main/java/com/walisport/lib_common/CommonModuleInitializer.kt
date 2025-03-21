package com.walisport.lib_common

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib_base.BuildConfig
import com.walisport.lib_base.utils.LogUtils
import com.walisport.lib_base.utils.LogUtilsExt
import com.walisport.lib_base.utils.LogUtilsExt.logd
import com.walisport.lib_common.websocket.ISocket
import com.walisport.lib_common.websocket.SocketClientService
import com.walisport.lib_common.websocket.WebSocketManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import java.lang.ref.WeakReference

class CommonModuleInitializer : Initializer<String> {
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
        factory<ISocket<*,*,*>> { SocketClientService(context = WeakReference(androidContext())) }
        single { WebSocketManager(get()) }
    }

    private val moduleList:List<Module> = listOf(socketModules)
}
