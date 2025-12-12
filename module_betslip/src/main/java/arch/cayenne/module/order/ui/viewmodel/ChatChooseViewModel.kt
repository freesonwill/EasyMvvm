package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.MsgType

/**
 * @author: wenxi
 * @date: 11/12/25 20:55
 * @description:
 */
class ChatChooseViewModel:BaseViewModel() {
    private val _betClickLiveData:MutableLiveData<MsgType> = MutableLiveData()
    val betClickLiveData:LiveData<MsgType> = _betClickLiveData

    /**
     * type 0 game 1 sport
     * */
    fun clickBtn(type:MsgType){
        _betClickLiveData.value = type
    }
}