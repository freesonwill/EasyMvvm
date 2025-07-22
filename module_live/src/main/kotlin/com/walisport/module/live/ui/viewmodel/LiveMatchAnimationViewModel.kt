package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.data.repository.LiveMatchAnimationRepository
import com.walisport.module.live.data.repository.LiveVideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * LiveMatchAnimationFragment对应的ViewModel
 *
 */
class LiveMatchAnimationViewModel(
    private val repo: LiveMatchAnimationRepository
) : BaseViewModel() {

    //比赛状态
    private val _animationLiveUrl = UnPeekLiveData<String?>()
    val animationLiveUrl: UnPeekLiveData<String?> = _animationLiveUrl

    private val _liveVideoBean = MutableLiveData<LiveVideoBean>()
    val liveVideoBean: LiveData<LiveVideoBean> get() = _liveVideoBean

    private var job: Job? = null

    private var liveBeanJob: Job? = null

    fun matchId() = repo.matchId

    fun setMatchId(matchId: Long) {
        repo.matchId = matchId
    }

    fun createObserver() {
        job?.cancel()
        job = viewModelScope.launch {
            repo.observeAnimationLiveUrl(repo.matchId).collect {
                it?.let {
                    _animationLiveUrl.value = it
                }
            }
        }

        liveBeanJob?.cancel()
        liveBeanJob = viewModelScope.launch {
            repo.observeLiveVideoBean(repo.matchId).collect {
                if (it != null) {
                    _liveVideoBean.value = it
                }
            }
        }

    }


}