package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import com.walisport.module.live.data.repository.LiveVideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 竖屏播放视频时， 视频fragment对应的ViewModel
 */
class VideoMainViewModel(
    private val repo: LiveVideoRepository
) : BaseViewModel() {

    //比赛ID
    private val _matchId = MutableLiveData<Long>(0)
    val matchId: LiveData<Long> = _matchId

    //比赛状态
    private val _matchBeanLiveData = MutableLiveData<LiveMatchBean>()
    val matchBeanLiveData: LiveData<LiveMatchBean> = _matchBeanLiveData


    fun matchId() = repo.matchId

    fun setMatchId(matchId: Long) {
        repo.matchId = matchId
    }

    fun createObserver() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeMatchBean(repo.matchId).collect { matchBean ->

                matchBean?.let { match ->
                    withContext(Dispatchers.Main) {
//                    "match.${match}".logd("matchIssue")
                        _matchBeanLiveData.value = match

                    }
                }

            }
        }

    }


}