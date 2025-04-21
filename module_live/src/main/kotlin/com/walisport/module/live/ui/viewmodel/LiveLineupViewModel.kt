package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.data.LiveLineupRepository
import galaxy.client.proto.Sloth
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
class LiveLineupViewModel : BaseViewModel() {
    private val repository: LiveLineupRepository by inject { parametersOf(viewModelScope) }
    private val _matchLineupDetail = MutableLiveData<Sloth.MatchLineupDetail?>()
    val matchLineupDetail: LiveData<Sloth.MatchLineupDetail?> = _matchLineupDetail
    fun geMatchLineupDetail(matchId: Long ){
        viewModelScope.launch {
            _matchLineupDetail.value =  repository.getMatchLiveReq(matchId)
        }
    }
}