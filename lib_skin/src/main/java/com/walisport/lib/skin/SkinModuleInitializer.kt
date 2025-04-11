package com.walisport.lib.skin

import android.content.Context
import androidx.startup.Initializer
import arch.cayenne.lib.base.ApplicationModuleInitializer
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class SkinModuleInitializer : Initializer<String> {
    private val TAG: String = this.javaClass.simpleName
    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java)
    }
    private val socketModules = module {
        single<SportSkinManager> { SportSkinManager() }
    }
    private val moduleList: List<Module> = listOf(socketModules)

}