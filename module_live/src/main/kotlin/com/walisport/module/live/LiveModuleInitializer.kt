package com.walisport.module.live

import android.content.Context
import androidx.startup.Initializer
import com.walisport.lib.base.ApplicationModuleInitializer
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import com.walisport.module.live.viewmodel.VideoActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import com.walisport.module.live.ui.viewmodel.LiveBetSlipUnsettledViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipConfirmViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipInvalidViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipReserveViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipSettledViewModel


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
        viewModelOf(::LiveBetSlipViewModel)
        viewModelOf(::LiveBetSlipUnsettledViewModel)
        viewModelOf(::LiveBetSlipUnsettledViewModel)
        viewModelOf(::LiveBetSlipConfirmViewModel)
        viewModelOf(::LiveBetSlipInvalidViewModel)
        viewModelOf(::LiveBetSlipReserveViewModel)
        viewModelOf(::LiveBetSlipSettledViewModel)


    }
    private val repoModules = module {
        factoryOf(::LiveMainRepository)
    }
    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}