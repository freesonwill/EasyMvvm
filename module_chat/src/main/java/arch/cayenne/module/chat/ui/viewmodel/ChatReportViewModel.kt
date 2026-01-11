package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.module.chat.manager.ChatServerController
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * @author: wenxi
 * @date: 4/11/25 14:37
 * @description:
 */
class ChatReportViewModel:BaseViewModel() {
    private val chatServer: ChatServerController by inject { parametersOf(viewModelScope) }

    val reportUserFlow = chatServer.reportUserFlow


    fun reportOther(uid:String, chatType: ChatType, type:Int){
        chatServer.reportUser(uid,chatType,type)
    }

}