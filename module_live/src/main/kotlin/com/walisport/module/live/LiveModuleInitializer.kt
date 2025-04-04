package com.walisport.module.live

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib.base.ApplicationModuleInitializer
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import com.walisport.module.live.viewmodel.VideoActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class LiveModuleInitializer : Initializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java)
    }

    private val viewModules = module {
        viewModelOf(::LiveMainViewModel)
        viewModelOf(::LiveVideoViewModel)
        viewModelOf(::VideoActivityViewModel)
    }
    private val repoModules = module {
        factoryOf(::LiveMainRepository)
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}