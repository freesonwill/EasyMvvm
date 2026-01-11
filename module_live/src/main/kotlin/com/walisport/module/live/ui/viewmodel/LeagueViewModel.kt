package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.data.repository.LiveLeagueRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LeagueViewModel : BaseViewModel() {

    private val repo: LiveLeagueRepository by inject { parametersOf(viewModelScope) }
    private val _leagueData = MutableLiveData<LeagueMatchBean>()
    val leagueData: LiveData<LeagueMatchBean> get() = _leagueData
    private val _activeHeaderIndex = MutableLiveData<Int?>()
    val activeHeaderIndex: LiveData<Int?> get() = _activeHeaderIndex
    fun observeLoginChange() = repo.observeLoginChange()
    private var cursorMatchId: Long = 0L
    private var cursorMatchStartTime: Long = 0L

    fun getMatchLeagueList(leagueId: Int) {
        cursorMatchId = 0L
        cursorMatchStartTime = 0L
        callApi({
            repo.getMatchLeagueList(leagueId, cursorMatchId, cursorMatchStartTime)
        }, {
            if (it is ApiResponseState.Failed) {
                setState(DataState.NetworkUnavailable)
            } else if (it is ApiResponseState.Succeeded<*>) {
                val data = it.data!! as LeagueMatchBean
                _leagueData.value = data
                if (data.match.isNotEmpty()) {
                    cursorMatchId = data.match.last().matchId
                    cursorMatchStartTime = data.match.last().startTime
                    setState(DataState.LoadSuccess)
                    //当接口成功返回数据但数量少于10条即可认为已经到底了
                    if (data.match.size < 10) {
                        setState(DataState.NoMoreData)
                    }
                } else {
                    setState(DataState.NoMoreData)
                }
            }
        }, autoUpdateState = false)
    }

    fun getMoreMatchLeagueList(leagueId: Int) {
        callApi({
            repo.getMatchLeagueList(leagueId, cursorMatchId, cursorMatchStartTime)
        }, {
            if (it is ApiResponseState.Failed) {
                setState(DataState.NetworkUnavailable)
            } else if (it is ApiResponseState.Succeeded<*>) {
                val data = it.data!! as LeagueMatchBean
                val list = _leagueData.value!!.match.toMutableList()
                list.addAll(data.match)
                val tmp = LeagueMatchBean(
                    match = list,
                    tournamentName = data.tournamentName,
                    tournamentShortName = data.tournamentShortName,
                    size = data.size,
                    logo = data.logo,
                    color = data.color
                )
                _leagueData.value = tmp
                if (data.match.isNotEmpty()) {
                    cursorMatchId = data.match.last().matchId
                    cursorMatchStartTime = data.match.last().startTime
                    setState(DataState.LoadSuccess)
                    //当接口成功返回数据但数量少于10条即可认为已经到底了
                    if (data.match.size < 10) {
                        setState(DataState.NoMoreData)
                    }
                } else {
                    setState(DataState.NoMoreData)
                }
            }
        }, autoUpdateState = false)
    }

    fun setActiveHeaderIndex(index: Int?) {
        if (_activeHeaderIndex.value != index) {
            _activeHeaderIndex.value = index
        }
    }
}