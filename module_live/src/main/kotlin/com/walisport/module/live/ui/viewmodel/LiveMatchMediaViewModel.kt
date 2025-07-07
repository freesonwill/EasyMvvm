package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import com.walisport.module.live.data.repository.LiveVideoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 *
 * LiveMatchMediaFragment对应的ViewModel
 *
 */
class LiveMatchMediaViewModel(
    private val repo: LiveVideoRepository
) : BaseViewModel() {

    //比赛状态
    private val _matchBeanLiveData = MutableLiveData<LiveMatchBean>()
    val matchBeanLiveData: LiveData<LiveMatchBean> = _matchBeanLiveData

    private val _animationSwitch: UnPeekLiveData<Boolean> = UnPeekLiveData(false)
    val animationSwitch: UnPeekLiveData<Boolean> = _animationSwitch

    private val _chooseSource: UnPeekLiveData<Boolean> = UnPeekLiveData(false)
    val chooseSource: UnPeekLiveData<Boolean> = _chooseSource

    private var job: Job? = null

    fun matchId() = repo.matchId

    fun setMatchId(matchId: Long) {
        repo.matchId = matchId
    }

    fun createObserver() {
        job?.cancel()
        job = viewModelScope.launch {
            repo.observeMatchBean(repo.matchId).collect {
                it?.let {
                    _matchBeanLiveData.value = it
                }
            }
        }

    }

    fun switchToAnimation() {
        _animationSwitch.value = true
    }

    fun chooseSourceView() {
        _chooseSource.value = true
    }


}