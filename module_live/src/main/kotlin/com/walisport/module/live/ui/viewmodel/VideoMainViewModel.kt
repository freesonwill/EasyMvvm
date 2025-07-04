package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import com.walisport.module.live.data.repository.LiveVideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 竖屏播放视频时， 视频fragment对应的ViewModel
 */
class VideoMainViewModel(
    private val repo: LiveVideoRepository
) : BaseViewModel() {

    //比赛状态
    private val _matchBeanLiveData = MutableLiveData<LiveMatchBean>()
    val matchBeanLiveData: LiveData<LiveMatchBean> = _matchBeanLiveData

    private var job: Job? = null

    fun matchId() = repo.matchId

    fun setMatchId(matchId: Long) {
        repo.matchId = matchId
    }

    fun createObserver() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            repo.observeMatchBean(repo.matchId).collect { matchBean ->

                matchBean?.let { match ->
                    withContext(Dispatchers.Main) {
                        _matchBeanLiveData.value = match

                    }
                }

            }
        }

    }


}