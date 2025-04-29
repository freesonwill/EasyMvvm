package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.data.repository.LiveOutsRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveOutsViewModel : BaseViewModel() {

    private val repository: LiveOutsRepository by inject { parametersOf(viewModelScope) }
    private val _liveOutsData = MutableLiveData<MatchTrendData>()
    val liveOutsData: LiveData<MatchTrendData> = _liveOutsData

    fun getMatchTrendData(matchId: Long) {
        viewModelScope.launch {
            val result = repository.getMatchTrendReq(matchId)
            _liveOutsData.value = result
        }
    }
}