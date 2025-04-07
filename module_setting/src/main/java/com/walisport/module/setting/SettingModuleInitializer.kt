package com.walisport.module.setting

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib.base.ApplicationModuleInitializer
import com.walisport.module.setting.data.SettingRepository
import com.walisport.module.setting.data.SettingViewModel
import com.walisport.module.setting.data.NoticeViewModel
import com.walisport.module.setting.data.LanguageViewModel
import com.walisport.module.setting.data.BackgroundViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class SettingModuleInitializer : Initializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(ApplicationModuleInitializer::class.java)
    }

    private val viewModules = module {
        viewModelOf(::SettingViewModel)
        viewModelOf(::NoticeViewModel)
        viewModelOf(::LanguageViewModel)
        viewModelOf(::BackgroundViewModel)
    }
    private val repoModules = module {
        factory { (scope: CoroutineScope) -> SettingRepository(scope) }
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}