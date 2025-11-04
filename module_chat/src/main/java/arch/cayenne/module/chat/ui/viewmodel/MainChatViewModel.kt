package arch.cayenne.module.chat.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * @author: wenxi
 * @date: 6/10/25 17:47
 * @description:
 */
class MainChatViewModel:BaseViewModel() {
    private val _jump2CustomerService = MutableSharedFlow<Boolean>(replay = 1)
    val jump2CustomerService:SharedFlow<Boolean> = _jump2CustomerService.asSharedFlow()


    /**
     * 跳转客服
     */
    fun jump2CustomerService(b:Boolean){
        _jump2CustomerService.tryEmit(b)
    }
}