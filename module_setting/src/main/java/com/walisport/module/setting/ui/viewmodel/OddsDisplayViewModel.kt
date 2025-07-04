package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.setting.data.OddsDisplayEnum
import com.walisport.module.setting.data.OddsDisplayRepository
import kotlinx.coroutines.launch

class OddsDisplayViewModel(private val repo: OddsDisplayRepository): BaseViewModel() {

    private val _displayType = MutableLiveData<OddsDisplayEnum>()
    val displayType: LiveData<OddsDisplayEnum> get() = _displayType

    init {
        viewModelScope.launch {
            _displayType.value = repo.getOddsType()
        }
    }

    //设置赔率方式
    fun setOddsType(type: OddsDisplayEnum) {
        callApi( {
            repo.setOddsType(type)
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                _displayType.value = type
            }
        })
    }
}