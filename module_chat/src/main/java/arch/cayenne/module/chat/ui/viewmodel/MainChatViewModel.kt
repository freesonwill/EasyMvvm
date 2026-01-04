package arch.cayenne.module.chat.ui.viewmodel

import android.service.autofill.UserData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.module.chat.utils.ChatMsgUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.inject

/**
 * @author: wenxi
 * @date: 6/10/25 17:47
 * @description:
 */
class MainChatViewModel : BaseViewModel() {
    private val _jump2CustomerService = MutableSharedFlow<Boolean>(replay = 1)
    val jump2CustomerService: SharedFlow<Boolean> = _jump2CustomerService.asSharedFlow()
    val userDataManager:UserDataManager by inject()

    /**
     * 跳转客服
     */
    fun jump2CustomerService(b: Boolean) {
        _jump2CustomerService.tryEmit(b)
    }

    fun getMainChatLanguageRoom() :Long{
        var mainChatLanguagePosition = userDataManager.getValue(UserDataKey.MAIN_CHAT_LANGUAGE, -1)
        if (mainChatLanguagePosition == -1) {
            mainChatLanguagePosition =
                ChatMsgUtils.getLanguagePosition(ChatMsgUtils.getSystemLocale())
        }
        "getMainChatLanguageRoom $mainChatLanguagePosition".logd(TAG)
        return ChatMsgUtils.mainChatRoom()[mainChatLanguagePosition]
    }
}