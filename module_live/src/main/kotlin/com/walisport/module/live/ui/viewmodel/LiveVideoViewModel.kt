package com.walisport.module.live.ui.viewmodel

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.MuteManager
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.data.repository.LiveVideoRepository
import com.walisport.module.live.utils.LiveDateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * 竖屏播放视频时， 视频fragment对应的ViewModel
 */
class LiveVideoViewModel(
    private val repo: LiveVideoRepository,
    private val mainRepo: LiveMainRepository
) : BaseViewModel() {

    private val _mainMatch = MutableLiveData<LiveMatchBean>()
    val mainMatch: LiveData<LiveMatchBean> = _mainMatch

    //比赛状态
    private val _matchBeanLiveData = MutableLiveData<LiveMatchBean>()
    val matchBeanLiveData: LiveData<LiveMatchBean> = _matchBeanLiveData

    //主队名称
    private val _homeTeamName = MutableLiveData("")
    val homeTeamName: LiveData<String> = _homeTeamName

    //主队图标
    private val _homeTeamIcon = MutableLiveData<String>("")
    val homeTeamIcon: LiveData<String> = _homeTeamIcon

    //客队名称
    private val _awayTeamName = MutableLiveData("")
    val awayTeamName: LiveData<String> = _awayTeamName

    //客队图标
    private val _awayTeamIcon = MutableLiveData<String>("")
    val awayTeamIcon: LiveData<String> = _awayTeamIcon

    //比赛名称
    private val _matchName = MutableLiveData("")
    val matchName: LiveData<String> = _matchName

    //标题信息
    private val _titleText = MutableLiveData<String>("")
    val titleText: LiveData<String> = _titleText

    @ColorRes
    private val _titleTextColor = MutableLiveData<Int>(arch.cayenne.lib.common.R.color.white)

    @ColorRes
    val titleTextColor: LiveData<Int> = _titleTextColor

    @DimenRes
    private val _titleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_17)

    @DimenRes
    val titleTextSize: LiveData<Int> = _titleTextSize

    //副标题信息
    private val _subTitleText = MutableLiveData<String>("")
    val subTitleText: LiveData<String> = _subTitleText

    @ColorRes
    private val _subTitleTextColor = MutableLiveData<Int>(arch.cayenne.lib.common.R.color.color_929298)

    @ColorRes
    val subTitleTextColor: LiveData<Int> = _subTitleTextColor

    @DimenRes
    private val _subTitleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_14)

    @DimenRes
    val subTitleTextSize: LiveData<Int> = _subTitleTextSize

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
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeLiveVideoBean(repo.matchId).collect {
                withContext(Dispatchers.Main) {
                    if (it != null) {
                        _liveVideoBean.value = it
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repo.observeMatchBean(repo.matchId).collect { matchBean ->

                matchBean?.let { match ->
                    withContext(Dispatchers.Main) {
//                    "match.${match}".logd("matchIssue")
                        _matchBeanLiveData.value = match

                        _homeTeamName.value = match.basicInfo.homeTeam
                        _homeTeamIcon.value = match.basicInfo.homeTeamIcon
                        _awayTeamName.value = match.basicInfo.awayTeam
                        _awayTeamIcon.value = match.basicInfo.awayTeamIcon

                        //比赛名称
                        _matchName.value = match.basicInfo.matchName

                        //联赛图标
                        _tournamentIcon.value = match.basicInfo.tournamentIcon

                        val matchStatus =
                            MatchStatus.entries.find { it.code == matchBean.basicInfo.status }

                        matchStatus?.let {
                            when (it) {
                                MatchStatus.NOT_STARTED -> {
                                    val (date, time) = LiveDateUtil.getDisplay(matchBean.basicInfo.startTime)
                                    _titleText.value = date
                                    _titleTextColor.value = arch.cayenne.lib.common.R.color.white
                                    _titleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_17
                                    _subTitleText.value = time
                                    _subTitleTextColor.value =
                                        arch.cayenne.lib.common.R.color.color_666666
                                    _subTitleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_14
                                }

                                MatchStatus.IN_PROGRESS -> {
                                    _titleText.value = match.liveInfo.score
                                    _titleTextColor.value =
                                        arch.cayenne.lib.common.R.color.color_fe3666
                                    _titleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_24
                                    _subTitleText.value = "" // 比赛进行中不展示副标题
                                    _subTitleTextColor.value =
                                        arch.cayenne.lib.common.R.color.color_fe3666
                                    _subTitleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_14
                                }

                                else -> {
                                    _titleText.value = match.liveInfo.score
                                    _titleTextColor.value =
                                        arch.cayenne.lib.common.R.color.color_fe3666
                                    _titleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_24
                                    _subTitleText.value = when (it) {
                                        MatchStatus.FINISHED -> com.walisport.module.live.R.string.match_finished.getString()
                                        MatchStatus.POSTPONED -> com.walisport.module.live.R.string.match_postponed.getString()
                                        MatchStatus.INTERRUPTED -> com.walisport.module.live.R.string.match_interrupted.getString()
                                        MatchStatus.CANCELED -> com.walisport.module.live.R.string.match_cancelled.getString()
                                        MatchStatus.DELAYED -> com.walisport.module.live.R.string.match_delayed.getString()
                                        MatchStatus.ABANDONED -> com.walisport.module.live.R.string.match_abandoned.getString()
                                        MatchStatus.PAUSED -> com.walisport.module.live.R.string.match_suspended.getString()
                                        else -> "" // 防止遗漏
                                    }
                                    _subTitleTextColor.value =
                                        arch.cayenne.lib.common.R.color.color_fe3666
                                    _subTitleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_14
                                }
                            }
                        }

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
}