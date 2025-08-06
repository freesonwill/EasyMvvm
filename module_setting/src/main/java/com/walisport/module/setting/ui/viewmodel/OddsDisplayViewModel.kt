package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.OddsDisplayEnum
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import kotlinx.coroutines.launch

class OddsDisplayViewModel(private val manager: UserDataManager,): BaseViewModel() {

    private val _displayType = MutableLiveData<OddsDisplayEnum>()
    val displayType: LiveData<OddsDisplayEnum> get() = _displayType

    init {
        viewModelScope.launch {
            val value = manager.getValue(UserDataKey.KEY_ODDS, OddsDisplayEnum.EU.value)
            _displayType.value = OddsDisplayEnum.entries[value]
        }
    }

    //设置赔率方式
    fun setOddsType(type: OddsDisplayEnum) {
        manager.setKeyValue(UserDataKey.KEY_ODDS, type.value)
        _displayType.value = type
    }
}