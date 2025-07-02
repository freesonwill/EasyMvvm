package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.LiveMarketListBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.MarketMenuBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import com.walisport.module.live.data.repository.LiveBetOnRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import arch.cayenne.module.bet.repo.BetRepository
import com.walisport.module.live.data.toData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.koin.core.parameter.parametersOf

class LiveBetOnViewModel : BaseViewModel() {
    private val repository: LiveBetOnRepository by inject()
    private val betRepository: BetRepository by inject { parametersOf(viewModelScope) }
    private var observeJob: Job? = null
    private val _observeSelection = MutableSharedFlow<List<LiveSelectionBean>?>(replay = 1)
    val observeSelection: Flow<List<LiveSelectionBean>?> = _observeSelection

    private val _marketType = MutableLiveData<List<MarketTypeBean>?>()
    val marketType: LiveData<List<MarketTypeBean>?> = _marketType

    private val _marketMenu = MutableLiveData<List<MarketMenuBean>?>()
    val marketMenu: LiveData<List<MarketMenuBean>?> = _marketMenu

    private val _getMarketList = MutableLiveData<List<MarketMenuBean>?>()
    val getMarketList: LiveData<List<MarketMenuBean>?> = _getMarketList



    private val _liveMarketListBean = MutableLiveData<List<LiveMarketListBean>?>()
    val liveMarketListBean: LiveData<List<LiveMarketListBean>?> = _liveMarketListBean

    //监听盘口筛选变化
    private val _observeMarketMenu = MutableLiveData<MutableList<Int>>()
    val observeMarketMenu: LiveData<MutableList<Int>> = _observeMarketMenu

    //监听串关数据变化
    private val _observerSelectionCombo = MutableLiveData<Long?>()
    val observerSelectionCombo: LiveData<Long?> = _observerSelectionCombo
    fun getMarketType(matchId: Long) {
        repository.queryLiveMarketType(matchId, {
            _marketType.value = it
        }, {
            _marketMenu.value = it
        })
    }

    fun getMarketMenuByCode(code: String): List<MarketMenuBean> {
        return marketMenu.value?.filter { it.code == code }!!
    }

    //监听串关数据变化//监听串关数据变化
    fun observerSelectionComboByMatchId(matchId: Long) {
        viewModelScope.launch {
            betRepository.observerSelectionByMatchId(matchId).collect {
                _observerSelectionCombo.value = it
            }
        }
    }


    fun setMarketMenuPosition(titlePosition: Int, contentPosition: Int) {
        var position: MutableList<Int> = mutableListOf(titlePosition, contentPosition)
        _observeMarketMenu.value = position
    }

    //根据盘口分类code获取盘口列表
    fun getMarketList(code: String) {
     // LogUtils.dTag("盘口选择","-${code}---name${getMarketList.value?.find { it.code==code }?.marketName}")
       var codes = if (code.isEmpty()) {
            marketMenu.value
        } else {
            getMarketMenuByCode(code)
        }
        _getMarketList.value = codes
        var marketIds: MutableList<Long> = mutableListOf()
        codes?.forEach {
            marketIds.add(it.marketId)
        }
//        viewModelScope.launch(Dispatchers.IO) {
//            val list: MutableList<LiveSelectionBean> = mutableListOf()
//            marketIds.forEach {
//                list.addAll(repository.queryLiveSelectionBean(it))
//            }
//            val selections =  codes?.toData(list,code)
//            viewModelScope.launch(Dispatchers.Main) {
//                _liveMarketListBean.value = selections?.markets
//            }
//        }
        //监听盘口数据变化
       // LogUtils.d("监听盘口数据变化observeSelection${marketIds}")
        //订阅数据
        observeSelection(marketIds)
    }

    fun observeSelectionGetMarketList(code: String) {
         LogUtils.dTag("盘口推送","-${code}---name${getMarketList.value?.find { it.code==code }?.marketName}")
        var codes = if (code.isEmpty()) {
            marketMenu.value
        } else {
            getMarketMenuByCode(code)
        }
        _getMarketList.value = codes
        var marketIds: MutableList<Long> = mutableListOf()
        codes?.forEach {
            marketIds.add(it.marketId)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val list: MutableList<LiveSelectionBean> = mutableListOf()
            marketIds.forEach {
                list.addAll(repository.queryLiveSelectionBean(it))
            }
            val selections =  codes?.toData(list,code)
            viewModelScope.launch(Dispatchers.Main) {
                _liveMarketListBean.value = selections?.markets
            }
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
        // 取消之前的协程
        observeJob?.cancel()
        // 启动新的协程
        observeJob = viewModelScope.launch {
            repository.observeSelection(marketIds).collect {
                _observeSelection.emit(it)
              //  LogUtils.d("比赛详情--------observeSelection${it}")
            }
        }
    }
}
