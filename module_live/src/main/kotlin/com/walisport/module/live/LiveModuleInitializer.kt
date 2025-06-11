package com.walisport.module.live

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.live.data.LandscapeVideoFragmentLifeCycle
import com.walisport.module.live.data.LiveLineupRepository
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.MuteManager
import com.walisport.module.live.data.repository.LiveBetOnMenuRepository
import com.walisport.module.live.data.repository.LiveBetOnRepository
import com.walisport.module.live.data.repository.LiveChatRepository
import com.walisport.module.live.data.repository.LiveLeagueRepository
import com.walisport.module.live.data.repository.LiveStandingRepository
import com.walisport.module.live.data.repository.LiveVideoRepository
import com.walisport.module.live.ui.viewmodel.EmojiViewModel
import com.walisport.module.live.ui.viewmodel.LeagueViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetOnMenuViewModel
import com.walisport.module.live.ui.viewmodel.LiveBetOnViewModel
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveLineupViewModel
import com.walisport.module.live.ui.viewmodel.LiveOutsViewModel
import com.walisport.module.live.ui.viewmodel.LiveSoftKeyboardViewModel
import com.walisport.module.live.ui.viewmodel.LiveStandingsViewModel
import com.walisport.module.live.ui.viewmodel.LiveVideoSourceViewModel
import com.walisport.module.live.ui.viewmodel.LiveVideoViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class LiveModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        includes(defaultModule)
        viewModelOf(::LiveVideoViewModel)
        viewModelOf(::LiveChatViewModel)
        viewModelOf(::LeagueViewModel)
        viewModelOf(::LiveOutsViewModel)
        viewModelOf(::LiveBetOnViewModel)
        viewModelOf(::LiveLineupViewModel)
        viewModelOf(::LiveStandingsViewModel)
        viewModelOf(::LiveSoftKeyboardViewModel)
        viewModelOf(::EmojiViewModel)
        viewModelOf(::LiveBetOnMenuViewModel)
        viewModelOf(::LiveVideoSourceViewModel)
    }

    private val repoModules = module {
        factoryOf(::LiveMainRepository)
        factoryOf(::LiveLineupRepository)
        factoryOf(::LiveVideoRepository)
        factoryOf(::LiveStandingRepository)
        factoryOf(::LiveBetOnRepository)
        factoryOf(::LiveBetOnMenuRepository)
        factoryOf(::LiveLeagueRepository)
        factoryOf(::LiveChatRepository)
    }

    private val managerModule = module {
        factoryOf(::LiveRemoteManager)
        factoryOf(::LiveRemoteChatManager)
        singleOf(::MuteManager)
        singleOf(::LandscapeVideoFragmentLifeCycle)
    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}