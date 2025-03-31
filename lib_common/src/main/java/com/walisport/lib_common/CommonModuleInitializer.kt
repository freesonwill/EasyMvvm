package com.walisport.lib_common

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib_base.utils.LogUtilsExt.logd
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module

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

    private val moduleList:List<Module> = listOf()
}
