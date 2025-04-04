package com.walisport.lib.skin

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib.base.ApplicationModuleInitializer
import com.walisport.lib.skin.res.SportSkinResourceManager
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
        single<SportSkinResourceManager> { SportSkinResourceManager()}
        single<SportSkinManager> { SportSkinManager() }
    }
    private val moduleList: List<Module> = listOf(socketModules)

}