package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.data.repository.LiveBetOnMenuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveBetOnMenuViewModel : BaseViewModel() {
    private val repository: LiveBetOnMenuRepository by inject { parametersOf(viewModelScope) }
    private val _marketType = MutableLiveData<List<MarketTypeBean>?>()
    val marketType: LiveData<List<MarketTypeBean>?> = _marketType

    private val _marketMenu = MutableLiveData<List<MarketMenuBean>?>()
    val marketMenu: LiveData<List<MarketMenuBean>?> = _marketMenu

    fun getMarketType() {
        viewModelScope.launch(IO) {
            try {
                val results = repository.queryLiveMarketMenuByCode()
                _marketMenu.postValue(results)
                val result = repository.queryLiveMarketType()
                _marketType.postValue(result)
            } catch (e: Exception) {
                _marketType.postValue(null)
            }
        }
    }

    fun getMarketMenuByCode(code: String,marketBeanMenCallback: (List<MarketMenuBean>) -> Unit) {
        var data : List<MarketMenuBean> = marketMenu.value?.filter { it.code == code }!!
        marketBeanMenCallback(data)
    }
}