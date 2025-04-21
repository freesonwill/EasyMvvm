package com.walisport.module.live

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.LiveLineupRepository
import com.walisport.module.live.data.MuteManager
import com.walisport.module.live.ui.viewmodel.EmojiViewModel
import com.walisport.module.live.ui.viewmodel.LeagueViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetOnMenuViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipConfirmViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipExpiredViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipReserveViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipSettledViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipUnsettledViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetSlipViewModel
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveLineupViewModel
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import com.walisport.module.live.ui.viewmodel.LiveSoftKeyboardViewModel
import com.walisport.module.live.ui.viewmodel.LiveStandingsViewModel
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import com.walisport.module.live.viewmodel.LiveMainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.walisport.module.live.data.LiveBetRepository


class LiveModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        viewModelOf(::LiveMainViewModel)
        viewModelOf(::LiveVideoViewModel)
        viewModelOf(::LiveBetSlipViewModel)
        viewModelOf(::LiveChatViewModel)
        viewModelOf(::LeagueViewModel)
        viewModelOf(::LiveOutsViewModel)
        viewModelOf(::LiveBetOnViewModel)
        viewModelOf(::LiveLineupViewModel)
        viewModelOf(::LiveStandingsViewModel)
        viewModelOf(::LiveBetSlipUnsettledViewModel)
        viewModelOf(::LiveBetSlipUnsettledViewModel)
        viewModelOf(::LiveBetSlipConfirmViewModel)
        viewModelOf(::LiveBetSlipExpiredViewModel)
        viewModelOf(::LiveBetSlipReserveViewModel)
        viewModelOf(::LiveBetSlipSettledViewModel)
        viewModelOf(::LiveSoftKeyboardViewModel)
        viewModelOf(::EmojiViewModel)
        viewModelOf(::LiveBetOnMenuViewModel)

    }
    private val repoModules = module {
        factoryOf(::LiveMainRepository)
        factoryOf(::LiveBetRepository)
        factoryOf(::LiveLineupRepository)
    }

    private val managerModule = module {
        factoryOf(::LiveRemoteManager)
        singleOf(::MuteManager)
    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}