package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.data.repository.LiveBetOnRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class LiveBetOnViewModel : BaseViewModel() {
    private val repository: LiveBetOnRepository by inject()
    private val _observeMarketType = MutableLiveData<List<MarketTypeBean>?>()
    val observeMarketType: LiveData<List<MarketTypeBean>?> = _observeMarketType

    private val _marketType = MutableLiveData<List<MarketTypeBean>?>()
    val marketType: LiveData<List<MarketTypeBean>?> = _marketType

    private val _getMarketList = MutableLiveData<List<MarketMenuBean>?>()
    val getMarketList: LiveData<List<MarketMenuBean>?> = _getMarketList

    private val _getLiveSelectionBean = MutableLiveData<Map<Long, List<LiveSelectionBean>>>()
    val getLiveSelectionBean: LiveData<Map<Long, List<LiveSelectionBean>>> = _getLiveSelectionBean

    fun getMarketType(matchId: Long) {
        viewModelScope.launch {
            repository.queryLiveMarketType(matchId)
        }
    }

    //获取所有
    fun getMarketTypeAll() {
        viewModelScope.launch {
            _marketType.value = repository.queryLiveMarketTypeAll()
        }
    }

    //根据盘口分类code获取盘口列表
    fun getMarketList(code: String) {
        if (code.isEmpty()) {
            val list: MutableList<MarketMenuBean> = mutableListOf()
            observeMarketType.value?.forEach {
                list.addAll(it.marketMenuBean)
            }
            _getMarketList.value = list
        } else {
            _getMarketList.value = observeMarketType.value?.find { it.code == code }?.marketMenuBean
        }

    }

    fun observeMarketTypeBean() {
        viewModelScope.launch {
            repository.observeMarketTypeBean().collect {
                _observeMarketType.value = it
            }
        }
    }

    fun getLiveSelectionBean(marketIds: List<Long>) {
        viewModelScope.launch {
            var map: MutableMap<Long, List<LiveSelectionBean>> = mutableMapOf()
            marketIds.forEach {
                map[it] = repository.queryLiveSelectionBean(it)
            }
            _getLiveSelectionBean.value = map
        }
    }
}