package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.data.repository.LiveBetOnMenuRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveBetOnMenuViewModel : BaseViewModel() {
    private val repository: LiveBetOnMenuRepository by inject { parametersOf(viewModelScope) }
    private val _marketType = MutableLiveData<List<MarketTypeBean>?>()
    val marketType: LiveData<List<MarketTypeBean>?> = _marketType

    private val _updateMarket = MutableLiveData<Int>()
    val updateMarket: LiveData<Int> = _updateMarket

    fun getMarketType() {
        viewModelScope.launch {
            _marketType.value = repository.queryLiveMarketType()
        }
    }

    //改变选择的颜色
    fun setMarketSelect(marketID: Long, selectId: Long, bool: Boolean) {
        viewModelScope.launch {
            _updateMarket.value = repository.updateMarketIdByMarketSelect(marketID, bool, selectId)
        }
    }
}