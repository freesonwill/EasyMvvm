package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.model.MatchHalfTeamStats
import com.walisport.module.live.data.model.MatchLiveData
import com.walisport.module.live.data.model.Stat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import plugin.koin.KoinViewModel

@KoinViewModel
class LiveMainViewModel(private val repo: LiveMainRepository) : BaseViewModel() {

    var matchId: Long = 0
    var sportId: Int = 0
    var leagueID: Int = 0
    var leagueName: String = ""
    var leagueLogo: String = ""

    //首次加载
    private val _mainMatch = MutableLiveData<LiveMatchBean>()
    val mainMatch: LiveData<LiveMatchBean> = _mainMatch

    //技术统计
    private val _statisticData = MutableLiveData<MatchLiveData>()
    val statisticData: LiveData<MatchLiveData> = _statisticData

    //监听数据变化
    private val _observeMainMatch = MutableLiveData<LiveMatchBean>()
    val observeMainMatch: LiveData<LiveMatchBean> = _observeMainMatch
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
            repo.getMatchRes(matchId) {
                _mainMatch.value = it
            }
        }
    }

    fun observeMatchBean(matchId: Long) {
        viewModelScope.launch {
            repo.observeMatchBean(matchId).collect {
                _observeMainMatch.value = it
            }
        }
    }

    fun registerMatchInfoNotify(matchId: Long) {
        viewModelScope.launch {
            repo.registerMatchInfoNotify(matchId)
            repo.observeMatchInfoNotify()
        }
    }

    fun unregisterMatchInfoNotify(matchId: Long) {
        viewModelScope.launch {
            repo.unregisterMatchInfoNotify(matchId)
        }
    }

    fun clearAllMatch() {
        viewModelScope.launch {
            repo.clearAllMatch()
        }
    }

    //订阅比赛技术统计推送
    fun registerStatisticsNotify(matchId: Long) {
        viewModelScope.launch {
            repo.registerMatchStaticsNotify(matchId)
        }
    }

    //取消订阅比赛技术统计推送
    fun unregisterStatisticsNotify() {
        viewModelScope.launch {
            repo.unregisterStatisticsNotify()
        }
    }

    //监听比赛技术统计推送
    fun observeMatchStaticsNotify() {
        viewModelScope.launch {
            repo.observeMatchStaticsNotify().collect {
                val teams = it.matchLiveData?.teamStatsList?.mapIndexed { _, item ->
                    MatchHalfTeamStats(
                        type = item.type,
                        homeNum = item.homeNum,
                        awayNum = item.awayNum
                    )
                } ?: emptyList()
                val stats = it.matchLiveData?.statsList?.mapIndexed { _, item ->
                    Stat(
                        type = item.type,
                        home = item.home,
                        away = item.away
                    )
                } ?: emptyList()
                val temp = MatchLiveData(0, teams, stats)
                _statisticData.value = temp
            }
        }
    }
}