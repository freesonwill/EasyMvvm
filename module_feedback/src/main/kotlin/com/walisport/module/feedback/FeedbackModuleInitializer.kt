package com.walisport.module.feedback

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.feedback.data.FeedbackMainRepository
import com.walisport.module.feedback.ui.viewmodel.FeedbackMainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class FeedbackModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        viewModelOf(::FeedbackMainViewModel)
    }

    private val repoModules = module {
        factoryOf(::FeedbackMainRepository)

    }

    private val managerModule = module {
        factoryOf(::FeedbackRemoteManager)

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}