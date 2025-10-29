package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveVideoBean
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

    //切到视频播放页面
    private val _switchToVideo: UnPeekLiveData<Boolean> = UnPeekLiveData(false)
    val switchToVideo: UnPeekLiveData<Boolean> = _switchToVideo

    //切到比赛状态页
    private val _switchToMatchStatus: UnPeekLiveData<Boolean> = UnPeekLiveData(false)
    val switchToMatchStatus: UnPeekLiveData<Boolean> = _switchToMatchStatus

    private val _animationLiveUrl = UnPeekLiveData<String?>()
    val animationLiveUrl: UnPeekLiveData<String?> = _animationLiveUrl

    private val _liveVideoBean = MutableLiveData<LiveVideoBean>()
    val liveVideoBean: LiveData<LiveVideoBean> get() = _liveVideoBean

    private var job: Job? = null

    private var animationUrlJob: Job? = null

    private var liveBeanJob: Job? = null

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

        animationUrlJob?.cancel()
        animationUrlJob = viewModelScope.launch {
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

    fun switchToAnimation() {
        _animationSwitch.value = true
    }

    fun chooseSourceView() {
        _chooseSource.value = true
    }

    fun switchToVideo() {
        _switchToVideo.value = true
    }

    fun switchToMatchStatus() {
        _switchToMatchStatus.value = true
    }

    fun queryLiveStream() {
        repo.queryLiveStream()
    }

    fun setPlayingVideoId(id: Int) {
        repo.setPlayingVideoId(id)
    }


}