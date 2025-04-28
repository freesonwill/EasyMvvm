package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.data.repository.LiveBetOnRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveBetOnViewModel : BaseViewModel() {
    private val repository: LiveBetOnRepository by inject { parametersOf(viewModelScope) }
    private val _marketType = MutableLiveData<List<MarketTypeBean>?>()
    val marketType: LiveData<List<MarketTypeBean>?> = _marketType
    fun getMarketType(matchId: Long) {
        viewModelScope.launch {
            repository.queryLiveMarketType(matchId)
            repository.observeLiveVideoBean().collect{
                _marketType.value = it
            }
        }
    }
}