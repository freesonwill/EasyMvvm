package com.walisport.lib.base

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib.base.BuildConfig
import com.walisport.lib.base.data.repository.EmptyRepository
import com.walisport.lib.base.data.viewmodel.EmptyViewModel
import com.walisport.lib.base.utils.LogUtils
import com.walisport.lib.base.utils.LogUtilsExt
import com.walisport.lib.base.utils.LogUtilsExt.logd
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * @author: zhangsan
 * @date: 2025/3/14 18:20
 * @description:
 */
class ApplicationModuleInitializer : Initializer<String> {
    private val TAG = this.javaClass.simpleName

    override fun create(context: Context):String {
        //LogUtils init
        LogUtils.getConfig()
            .setConsoleFilter(if (BuildConfig.DEBUG) LogUtils.V else LogUtils.V)
            .setTagPrefix(LogUtilsExt.TAG)
            .setBorderSwitch(false)
            .setLog2FileSwitch(true)
            .setSaveDays(7)
            .setLogHeadSwitch(false)
            .setSingleTagSwitch(false)

        //Koin init
        startKoin {
            androidLogger()
            androidContext(context)
            modules(moduleList)
        }
        "$TAG init....".logd(TAG)
        return TAG
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }


    private val viewModules = module {
        viewModelOf(::EmptyViewModel)
    }

    private val repoModules = module {
        singleOf(::EmptyRepository)
    }
    private val moduleList:List<Module> = listOf(viewModules,repoModules)
}
