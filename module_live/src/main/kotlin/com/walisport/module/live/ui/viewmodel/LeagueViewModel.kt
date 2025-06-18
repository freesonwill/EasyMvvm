package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.LeagueMatchBean
import com.walisport.module.live.data.repository.LiveLeagueRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LeagueViewModel : BaseViewModel() {

    private val repo: LiveLeagueRepository by inject { parametersOf(viewModelScope) }
    private val _leagueData = MutableLiveData<LeagueMatchBean?>()
    val leagueData: LiveData<LeagueMatchBean?> get() = _leagueData
    private var cursorMatchId: Long = 0L
    private var cursorMatchStartTime: Long = 0L

    fun getMatchLeagueData(leagueId: Int) {
        cursorMatchId = 0L
        cursorMatchStartTime = 0L
        viewModelScope.launch {
            val result = repo.getMatchLeagueData(leagueId, cursorMatchId, cursorMatchStartTime)
            result?.let {
                if (it.match.isNotEmpty()) {
                    cursorMatchId = it.match.last().matchId
                    cursorMatchStartTime = it.match.last().startTime
                }
            }
            _leagueData.value = result
        }
    }

    fun getMoreMatchLeagueData(leagueId: Int) {
        viewModelScope.launch {
            val result = repo.getMatchLeagueData(leagueId, cursorMatchId, cursorMatchStartTime)
            result?.let {
                if (it.match.isNotEmpty()) {
                    cursorMatchId = it.match.last().matchId
                    cursorMatchStartTime = it.match.last().startTime
                }
            }
            val list = _leagueData.value!!.match.toMutableList()
            val tmp = result?.let {
                list.addAll(it.match)
                LeagueMatchBean(
                    match = list,
                    tournamentName = it.tournamentName,
                    tournamentShortName = it.tournamentShortName,
                    logo = it.logo,
                    color = it.color
                )
            }
            _leagueData.value = tmp
        }
    }
}