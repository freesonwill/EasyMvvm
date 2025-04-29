package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.MuteManager
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.data.repository.LiveVideoRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * 竖屏播放视频时， 视频fragment对应的ViewModel
 */
class LiveVideoViewModel(
    private val repo: LiveVideoRepository,
    private val mainRepo: LiveMainRepository
) : BaseViewModel() {

    //比赛状态
    private val _matchStatusLiveData = MutableLiveData<MatchStatus?>()
    val matchStatusLiveData: LiveData<MatchStatus?> = _matchStatusLiveData

    //主队名称
    private val _homeTeamName = MutableLiveData("")
    val homeTeamName: LiveData<String> = _homeTeamName

    //主队图标
    private val _homeTeamIcon = MutableLiveData<String>("")
    val homeTeamIcon: LiveData<String> = _homeTeamIcon

    //客队名称
    private val _awayTeamName = MutableLiveData("阿根廷")
    val awayTeamName: LiveData<String> = _awayTeamName

    //客队图标
    private val _awayTeamIcon = MutableLiveData<String>("")
    val awayTeamIcon: LiveData<String> = _awayTeamIcon


    val titleText = MutableLiveData<String>("")
    val titleTextColor = MutableLiveData<Int>(arch.cayenne.lib.common.R.color.white)
    val titleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_17)

    val subTitleText = MutableLiveData<String>("")
    val subTitleTextColor = MutableLiveData<Int>(arch.cayenne.lib.res.R.color.color_929298)
    val subTitleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_14)

    private val _leagueImgSrc = MutableLiveData("")
    val leagueImgSrc: LiveData<String> = _leagueImgSrc

    private val _liveVideoBean = MutableLiveData<LiveVideoBean>()
    val liveVideoBean: LiveData<LiveVideoBean> get() = _liveVideoBean

    private val muteManager: MuteManager by inject { parametersOf() }

    fun mutedData() = muteManager.mutedLiveData

    fun changeMuteStatus() {
        viewModelScope.launch {
            muteManager.changeMuteStatus()
        }
    }

    fun matchId() = repo.matchId

    fun setMatchId(matchId: Long) {
        repo.matchId = matchId
    }

    fun createObserver() {
        viewModelScope.launch {
            repo.observeLiveVideoBean(repo.matchId).collect {
                if (it != null) {
                    _liveVideoBean.value = it
                }
            }
        }

        viewModelScope.launch {
            repo.observeMatchBean(repo.matchId).collect { matchBean ->
                matchBean?.let { match ->
//                    "match.${match}".logd("matchIssue")
                    _matchStatusLiveData.value =
                        MatchStatus.entries.find { it.code == match.basicInfo.status }
                    _homeTeamName.value = match.basicInfo.homeTeam
                    _homeTeamIcon.value = match.basicInfo.homeTeamIcon
                    _awayTeamName.value = match.basicInfo.awayTeam
                    _awayTeamIcon.value = match.basicInfo.awayTeamIcon
                }
            }
        }

    }


    fun queryLiveStream() {
        repo.queryLiveStream()
    }

}