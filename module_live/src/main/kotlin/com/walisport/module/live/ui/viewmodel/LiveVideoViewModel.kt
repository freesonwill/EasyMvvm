package com.walisport.module.live.ui.viewmodel

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.MatchBean
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.MuteManager
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.data.repository.LiveVideoRepository
import com.walisport.module.live.utils.LiveDateUtil
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
    private val _matchBeanLiveData = MutableLiveData<MatchBean>()
    val matchBeanLiveData: LiveData<MatchBean> = _matchBeanLiveData

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

    //标题信息
    private val _titleText = MutableLiveData<String>("")
    val titleText: LiveData<String> = _titleText
    @ColorRes
    private val _titleTextColor = MutableLiveData<Int>(arch.cayenne.lib.common.R.color.white)
    val titleTextColor: LiveData<Int> = _titleTextColor
    @DimenRes
    private val _titleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_17)
    val titleTextSize: LiveData<Int> = _titleTextSize

    //副标题信息
    private val _subTitleText = MutableLiveData<String>("")
    val subTitleText: LiveData<String> = _subTitleText
    @ColorRes
    private val _subTitleTextColor = MutableLiveData<Int>(arch.cayenne.lib.res.R.color.color_929298)
    val subTitleTextColor: LiveData<Int> = _subTitleTextColor
    @DimenRes
    private val _subTitleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_14)
    val subTitleTextSize: LiveData<Int> = _subTitleTextSize

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
                    _matchBeanLiveData.value = match

                    _homeTeamName.value = match.basicInfo.homeTeam
                    _homeTeamIcon.value = match.basicInfo.homeTeamIcon
                    _awayTeamName.value = match.basicInfo.awayTeam
                    _awayTeamIcon.value = match.basicInfo.awayTeamIcon

                    val matchStatus =
                        MatchStatus.entries.find { it.code == matchBean.basicInfo.status }

                    matchStatus?.let {
                        when (it) {
                            MatchStatus.FINISHED -> {
                                _titleText.value = match.liveInfo.score
                                _titleTextColor.value = arch.cayenne.lib.common.R.color.white
                                _titleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_17

                                _subTitleText.value = "比赛结束"
                                _subTitleTextColor.value = arch.cayenne.lib.res.R.color.color_666666
                                _subTitleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_14
                            }

                            MatchStatus.POSTPONED -> {

                            }

                            MatchStatus.INTERRUPTED -> {

                            }

                            MatchStatus.CANCELED -> {

                            }

                            MatchStatus.NOT_STARTED -> {
                                val (date, time) = LiveDateUtil.getDisplay(matchBean.basicInfo.startTime)

                                _titleText.value = date
                                _titleTextColor.value = arch.cayenne.lib.common.R.color.white
                                _titleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_17

                                _subTitleText.value = time
                                _subTitleTextColor.value = arch.cayenne.lib.res.R.color.color_666666
                                _subTitleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_14

                            }

                            MatchStatus.IN_PROGRESS -> {

                            }

                            MatchStatus.DELAYED -> {

                            }

                            MatchStatus.ABANDONED -> {

                            }

                            MatchStatus.PAUSED -> {

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

}