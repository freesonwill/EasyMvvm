package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.data.repository.LiveVideoRepository
import kotlinx.coroutines.launch

/**
 * 切换视频源页用到的ViewModel
 */
class LiveVideoSourceViewModel(
    private val repo: LiveVideoRepository,
) : BaseViewModel() {

    private val _liveVideoBean = MutableLiveData<LiveVideoBean>()
    val liveVideoBean: LiveData<LiveVideoBean> get() = _liveVideoBean

    fun setPlayingVideoId(id: Int) {
        repo.setPlayingVideoId(id)
    }

    fun matchId() = repo.matchId

    fun setMatchId(matchId: Long) {
        repo.matchId = matchId

        viewModelScope.launch {
            repo.observeLiveVideoBean(repo.matchId).collect {
                if (it != null) {
                    _liveVideoBean.value = it
                }
            }

        }
    }




}