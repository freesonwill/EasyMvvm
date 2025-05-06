package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.MarketTypeBean
import arch.cayenne.lib.database.entity.MatchBean
import com.walisport.module.live.data.LiveMainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import plugin.koin.KoinViewModel

@KoinViewModel
class LiveMainViewModel(private val repo: LiveMainRepository) : BaseViewModel() {

    var matchId: Long = 0
    var sportId: Int = 0
    private val _mainMatch = MutableLiveData<LiveMatchBean>()
    val mainMatch: LiveData<LiveMatchBean> = _mainMatch
    val currentBalanceChange by lazy { MutableLiveData<Long>() }

    override fun initViewModel() {
        super.initViewModel()

        //监听余额变化
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }

    }

    fun getMainMatch(matchId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getMatchRes(matchId)
        }
    }
    fun observeMatchBean(matchId: Long) {
        viewModelScope.launch {
            repo.observeMatchBean(matchId).collect {
                _mainMatch.value = it
            }
        }
    }
}