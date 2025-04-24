package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.data.LiveOutsRepository
import galaxy.client.proto.Sloth
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveOutsViewModel : BaseViewModel() {

    private val repository: LiveOutsRepository by inject { parametersOf(viewModelScope) }
    private val _liveOutsData = MutableLiveData<Sloth.MatchTrendData?>()
    val liveOutsData: LiveData<Sloth.MatchTrendData?> = _liveOutsData

    fun getMatchTrendData(matchId: Long) {
        viewModelScope.launch {
            val result = repository.getMatchTrendReq(matchId)
            _liveOutsData.value = result
        }
    }
}