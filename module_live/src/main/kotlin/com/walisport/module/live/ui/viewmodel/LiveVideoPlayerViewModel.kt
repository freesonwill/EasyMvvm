package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.data.LandscapeVideoFragmentLifeCycle
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.MuteManager
import com.walisport.module.live.data.repository.LiveVideoRepository
import com.xxx.qyplayer.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * 竖屏播放视频时， 视频fragment对应的ViewModel
 */
class LiveVideoPlayerViewModel(
    private val repo: LiveVideoRepository,
    private val mainRepo: LiveMainRepository
) : BaseViewModel() {

    private val _mainMatch = MutableLiveData<LiveMatchBean>()
    val mainMatch: LiveData<LiveMatchBean> = _mainMatch

    //比赛状态
    private val _matchBeanLiveData = MutableLiveData<LiveMatchBean>()
    val matchBeanLiveData: LiveData<LiveMatchBean> = _matchBeanLiveData


    //比赛名称
    private val _matchName = MutableLiveData("")
    val matchName: LiveData<String> = _matchName

    /**
     * 联赛图标
     */
    private val _tournamentIcon = MutableLiveData("")

    /**
     * 联赛图标
     */
    val tournamentIcon: LiveData<String> = _tournamentIcon

    private val _liveVideoBean = MutableLiveData<LiveVideoBean>()
    val liveVideoBean: LiveData<LiveVideoBean> get() = _liveVideoBean

    private val muteManager: MuteManager by inject { parametersOf() }

    fun mutedData() = muteManager.mutedLiveData

    private val landscapeVideoFragmentLifeCycle: LandscapeVideoFragmentLifeCycle by inject { parametersOf() }

    fun landscapeVideoFragmentDestroyedEvent() = landscapeVideoFragmentLifeCycle.destroyedEvent

    /**
     * 播放状态
     */
    private val _playerState = MutableLiveData(PlayerState.IDLE)
    val playerState: LiveData<PlayerState> get() = _playerState

    private var liveBeanJob: Job? = null

    private var matchBeanJob: Job? = null

    /**
     * 改变静音状态
     */
    fun changeMuteStatus() {
        viewModelScope.launch {
            muteManager.changeMuteStatus()
        }
    }

    /**
     * 设置静音
     */
    fun mute() {
        viewModelScope.launch { muteManager.mute() }
    }

    /**
     * 取消静音
     */
    fun unMute() {
        viewModelScope.launch { muteManager.unMute() }
    }

    var leagueID = 0

    fun matchId() = repo.matchId

    fun setMatchId(matchId: Long) {
        repo.matchId = matchId
    }

    fun createObserver() {
        liveBeanJob?.cancel()
        liveBeanJob = viewModelScope.launch(Dispatchers.IO) {
            repo.observeLiveVideoBean(repo.matchId).collect {
                withContext(Dispatchers.Main) {
                    if (it != null) {
                        _liveVideoBean.value = it
                    }
                }
            }
        }

        matchBeanJob?.cancel()
        matchBeanJob = viewModelScope.launch(Dispatchers.IO) {
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


    fun queryLiveStream() {
        repo.queryLiveStream()
    }

    fun getMainMatch(matchId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mainRepo.getMatchRes(matchId) {
                _mainMatch.value = it
            }
        }
    }

    fun setPlayerState(it: PlayerState) {
        _playerState.value = it
    }
}