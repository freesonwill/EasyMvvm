package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.skin.LanguageManager
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.ChatConfigDao
import arch.cayenne.module.chat.data.constants.EmojiEnum
import arch.cayenne.module.chat.data.model.EmojiModel

import arch.cayenne.module.chat.data.model.KeyBoardTabData

class SoftKeyboardViewModel : BaseViewModel() {

    val languageManager: LanguageManager by inject { parametersOf(viewModelScope) }
    val userDataManager: UserDataManager by inject()
    var keyBoardHeight: Int = 0
    //聊天设置
     val chatConfigDao: ChatConfigDao by inject()

    fun setSoftConfig(value:Boolean){
        userDataManager.setKeyValue(UserDataKey.KEY_SOFT_CONFIG,value)
    }





}
