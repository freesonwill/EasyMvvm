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
        viewModelOf(::ChatHomeViewModel)
        viewModelOf(::SoftKeyboardViewModel)
        viewModelOf(::ChatPageViewModel)
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