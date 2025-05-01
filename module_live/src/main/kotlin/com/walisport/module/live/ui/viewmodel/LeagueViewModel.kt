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

    private val repository: LiveLeagueRepository by inject { parametersOf(viewModelScope) }

    private val _leagueData = MutableLiveData<LeagueMatchBean?>()
    val leagueData: LiveData<LeagueMatchBean?> get() = _leagueData

    fun getMatchLeagueData(leagueId: Int) {
        viewModelScope.launch {
            val result = repository.getMatchLeagueData(leagueId)
            _leagueData.value = result
        }
    }
}