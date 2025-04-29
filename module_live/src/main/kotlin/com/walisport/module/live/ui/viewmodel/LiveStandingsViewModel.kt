package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.StandingsBean
import com.walisport.module.live.data.repository.LiveStandingRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveStandingsViewModel : BaseViewModel() {

    private val repository: LiveStandingRepository by inject { parametersOf(viewModelScope) }

    private val _competitionTables = MutableLiveData<List<StandingsBean>>()
    val competitionTables: LiveData<List<StandingsBean>> = _competitionTables

    fun getCompetitionData(matchId: Long) {
        viewModelScope.launch {
            val result = repository.getCompetitionReq(matchId)
            _competitionTables.value = result
        }
    }
}