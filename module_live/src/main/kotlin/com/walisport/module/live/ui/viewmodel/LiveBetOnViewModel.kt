package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.data.repository.LiveBetOnRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import arch.cayenne.module.bet.repo.BetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.core.parameter.parametersOf

class LiveBetOnViewModel : BaseViewModel() {
    private val repository: LiveBetOnRepository by inject()
    private val betRepository: BetRepository by inject { parametersOf(viewModelScope) }


    private val _observeSelection = MutableSharedFlow<List<LiveSelectionBean>?>(replay = 1)
    val observeSelection: Flow<List<LiveSelectionBean>?> = _observeSelection

    private val _marketType = MutableLiveData<List<MarketTypeBean>?>()
    val marketType: LiveData<List<MarketTypeBean>?> = _marketType

    private val _getMarketList = MutableLiveData<List<MarketMenuBean>?>()
    val getMarketList: LiveData<List<MarketMenuBean>?> = _getMarketList

    private val _getLiveSelectionBean =
        MutableSharedFlow<Map<Long, List<LiveSelectionBean>>>(replay = 1)
    val getLiveSelectionBean: Flow<Map<Long, List<LiveSelectionBean>>> = _getLiveSelectionBean

    //监听盘口筛选变化
    private val _observeMarketMenu = MutableLiveData<MutableList<Int>>()
    val observeMarketMenu: LiveData<MutableList<Int>> = _observeMarketMenu

    //监听串关数据变化
    private val _observerSelectionCombo = MutableLiveData<Long?>()
    val observerSelectionCombo: LiveData<Long?> = _observerSelectionCombo

    fun getMarketType(matchId: Long) {
        viewModelScope.launch {
            repository.queryLiveMarketType(matchId) {
                _marketType.value = it
            }
        }
    }
    //监听串关数据变化//监听串关数据变化
    fun observerSelectionComboByMatchId(matchId: Long){
        viewModelScope.launch {
            betRepository.observerSelectionByMatchId(matchId).collect {
                _observerSelectionCombo.value =it
            }
        }
    }

    fun setMarketMenuPosition(titlePosition: Int, contentPosition: Int) {
        var position: MutableList<Int> = mutableListOf(titlePosition, contentPosition)
        _observeMarketMenu.value = position
    }

    //根据盘口分类code获取盘口列表
    fun getMarketList(code: String) {
        if (code.isEmpty()) {
            val list: MutableList<MarketMenuBean> = mutableListOf()
            marketType.value?.forEach {
                list.addAll(it.marketMenuBean)
            }
            _getMarketList.value = list
        } else {
            _getMarketList.value = marketType.value?.find { it.code == code }?.marketMenuBean
        }
        var marketIds: MutableList<Long> = mutableListOf()
        _getMarketList.value?.forEach {
            marketIds.add(it.marketId)
        }
        //监听盘口数据变化
        LogUtils.e("observeSelection${marketIds}")
        observeSelection(marketIds)
    }

    fun observeSelectionGetMarketList(code: String) {
        if (code.isEmpty()) {
            val list: MutableList<MarketMenuBean> = mutableListOf()
            marketType.value?.forEach {
                list.addAll(it.marketMenuBean)
            }
            _getMarketList.value = list
        } else {
            _getMarketList.value = marketType.value?.find { it.code == code }?.marketMenuBean
        }
    }


    suspend fun setSelection(matchId: Long, selectionId: Long): AddSelectionStatus {
        val bean = repository.getSelectionInsertBean(matchId, selectionId)
        return if (bean == null) {
            AddSelectionStatus.FAIL
        } else {
            betRepository.setSelection(bean)
        }
    }

    //监听盘口数据变化
    fun observeSelection(marketIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeSelection(marketIds).collect {
                _observeSelection.tryEmit(it)
                LogUtils.e("比赛详情--------observeSelection${it}")
            }
        }
    }

    fun getLiveSelectionBean(marketIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            var map: MutableMap<Long, List<LiveSelectionBean>> = mutableMapOf()
            marketIds.forEach {
                map[it] = repository.queryLiveSelectionBean(it)
            }
            LogUtils.e("getLiveSelectionBean-----map---${map}")
            _getLiveSelectionBean.tryEmit(map)
        }
    }


}
