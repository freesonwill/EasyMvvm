package arch.cayenne.module.chat

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import arch.cayenne.module.chat.data.repository.LiveChatRepository
import arch.cayenne.module.chat.ui.viewmodel.ChatPageViewModel
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.ui.viewmodel.SoftKeyboardViewModel
import arch.cayenne.module.chat.ui.viewmodel.MainChatViewModel
import arch.cayenne.module.chat.ui.viewmodel.LivingEventsViewModel
import arch.cayenne.module.chat.ui.viewmodel.CustomerViewModel
import arch.cayenne.module.chat.ui.viewmodel.EmojiHomeViewModel
import arch.cayenne.module.chat.ui.viewmodel.EmojiViewModel
import arch.cayenne.module.chat.ui.viewmodel.ChatLanguageDialogViewModel
import arch.cayenne.module.chat.ui.viewmodel.ChatPersonalDialogViewModel
import arch.cayenne.module.chat.ui.viewmodel.ChatReportViewModel
import arch.cayenne.module.chat.ui.viewmodel.BetShareViewModel
import arch.cayenne.module.chat.ui.viewmodel.GameBetShareViewModel
import arch.cayenne.module.chat.ui.viewmodel.SportBetShareViewModel
import arch.cayenne.module.chat.manager.ChatManagerImpl
import arch.cayenne.module.chat.manager.ChatServerController
import kotlinx.coroutines.CoroutineScope


/**
 * @author: wenxi
 * @date: 26/9/25 16:42
 * @description:
 */
class ChatModuleInitializer : DefaultInitializer<String> {
    val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        includes(defaultModule)
        viewModelOf(::ChatHomeViewModel)
        viewModelOf(::SoftKeyboardViewModel)
        viewModelOf(::ChatPageViewModel)
        viewModelOf(::MainChatViewModel)
        viewModelOf(::LivingEventsViewModel)
        viewModelOf(::CustomerViewModel)
        viewModelOf(::EmojiHomeViewModel)
        viewModelOf(::EmojiViewModel)
        viewModelOf(::ChatLanguageDialogViewModel)
        viewModelOf(::ChatPersonalDialogViewModel)
        viewModelOf(::ChatReportViewModel)
        viewModelOf(::BetShareViewModel)
        viewModelOf(::GameBetShareViewModel)
        viewModelOf(::SportBetShareViewModel)
    }

    private val repoModules = module {
        factoryOf(::LiveChatRepository)
    }

    private val managerModule = module {
        factoryOf(::RemoteChatManager)
        factory { ChatManagerImpl(get()) }
        factory { (scope: CoroutineScope) -> ChatServerController(scope, get()) }
    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)

}