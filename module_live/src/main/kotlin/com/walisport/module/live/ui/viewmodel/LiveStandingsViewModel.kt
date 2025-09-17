package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.StandingsBean
import com.walisport.module.live.data.repository.LiveStandingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveStandingsViewModel : BaseViewModel() {

    private val repository: LiveStandingRepository by inject { parametersOf(viewModelScope) }

    private val _competitionTables = MutableLiveData<List<StandingsBean>>()
    val competitionTables: LiveData<List<StandingsBean>> = _competitionTables

    fun getCompetitionData(compId: Int) {
        viewModelScope.launch {
            //Tab切换动画和列表更新动画几乎同时发生引起卡顿感，加延迟确保tab切换动画执行完后再执行更新动画
            delay(200)
            val result = repository.getCompetitionReq(compId)
            _competitionTables.value = result
        }
    }
}