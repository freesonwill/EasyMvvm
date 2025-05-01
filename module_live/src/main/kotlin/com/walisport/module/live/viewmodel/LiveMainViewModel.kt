package com.walisport.module.live.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.MatchBean
import com.walisport.module.live.data.LiveMainRepository
import kotlinx.coroutines.launch
import plugin.koin.KoinViewModel

@KoinViewModel
class LiveMainViewModel(private val repo: LiveMainRepository) : BaseViewModel() {

    var matchId: Long = 0
    var sportId: Int = 0

    private val _matchMainMatch = MutableLiveData<MatchBean>()
    val matchMainMatch: LiveData<MatchBean> = _matchMainMatch

    fun geMatchMainMatch(matchId: Long) {
        viewModelScope.launch {
            val data = repo.getMatchBean(matchId)
            _matchMainMatch.value = data
        }
    }
}