package com.walisport.module.live.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import com.walisport.module.live.data.LiveMainRepository
import galaxy.client.proto.Sloth
import galaxy.common.proto.Common
import kotlinx.coroutines.launch

class LiveMainViewModel(private val repo: LiveMainRepository) : BaseViewModel() {

    var matchId:Long = 0
    var sportId:Int = 0
     var match : Common.Match? = null
    private val _matchMainMatch = MutableLiveData<Common.Match?>()
    val matchMainMatch: LiveData<Common.Match?> = _matchMainMatch
    fun geMatchMainMatch(matchId: Long) {
        viewModelScope.launch {
            val data = repo.getMatchReq(matchId)
            if (data != null) {
                match = data
                _matchMainMatch.value = match
            }
        }
    }
}