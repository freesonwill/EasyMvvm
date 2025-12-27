package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.ChatMsgType

/**
 * @author: wenxi
 * @date: 11/12/25 20:55
 * @description:
 */
class ChatChooseViewModel:BaseViewModel() {
    private val _betClickLiveData:MutableLiveData<ChatMsgType> = MutableLiveData()
    val betClickLiveData:LiveData<ChatMsgType> = _betClickLiveData

    /**
     * type 0 game 1 sport
     * */
    fun clickBtn(type:ChatMsgType){
        _betClickLiveData.value = type
    }
}