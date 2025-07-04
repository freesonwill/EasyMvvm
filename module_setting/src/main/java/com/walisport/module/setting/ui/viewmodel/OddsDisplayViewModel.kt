package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.setting.data.OddsDisplayEnum
import com.walisport.module.setting.data.OddsDisplayRepository
import kotlinx.coroutines.launch

class OddsDisplayViewModel(private val repo: OddsDisplayRepository): BaseViewModel() {

    private val _displayType = MutableLiveData<OddsDisplayEnum>()
    val displayType: LiveData<OddsDisplayEnum> get() = _displayType

    init {
        viewModelScope.launch {
            _displayType.value = getOddsType()
        }
    }

    //设置赔率方式
    fun setOddsType(type: OddsDisplayEnum) {
        repo.setOddsType(type.value)
        _displayType.value = type
        // TODO API success callback
    }

    //获取赔率显示方式
    private fun getOddsType(): OddsDisplayEnum {
        return OddsDisplayEnum.entries[repo.getOddsType()]
    }
}