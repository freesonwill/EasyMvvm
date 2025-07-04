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
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.data.repository.LiveVideoRepository
import com.walisport.module.live.utils.LiveDateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.walisport.module.live.R

/**
 * 竖屏播放视频时， 视频fragment对应的ViewModel
 */
class VideoMainViewModel(
    private val repo: LiveVideoRepository
) : BaseViewModel() {

    //比赛ID
    private val _matchId = MutableLiveData<Long>(0)
    val matchId: LiveData<Long> = _matchId

    private val _mainMatch = MutableLiveData<LiveMatchBean>()
    val mainMatch: LiveData<LiveMatchBean> = _mainMatch

    //比赛状态
    private val _matchBeanLiveData = MutableLiveData<LiveMatchBean>()
    val matchBeanLiveData: LiveData<LiveMatchBean> = _matchBeanLiveData

    //主队图标
    private val _homeTeamIcon = MutableLiveData<String>("")
    val homeTeamIcon: LiveData<String> = _homeTeamIcon

    //客队图标
    private val _awayTeamIcon = MutableLiveData<String>("")
    val awayTeamIcon: LiveData<String> = _awayTeamIcon

    //标题信息
    private val _titleText = MutableLiveData<String>("")

    @ColorRes
    private val _titleTextColor = MutableLiveData<Int>(arch.cayenne.lib.common.R.color.white)

    @DimenRes
    private val _titleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_17)

    //副标题信息
    private val _subTitleText = MutableLiveData<String>("")

    @ColorRes
    private val _subTitleTextColor = MutableLiveData<Int>(arch.cayenne.lib.common.R.color.color_929298)

    @DimenRes
    private val _subTitleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_14)

    private val _liveVideoBean = MutableLiveData<LiveVideoBean>()


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


                        val matchStatus =
                            MatchStatus.entries.find { it.code == matchBean.basicInfo.status }

                        matchStatus?.let {
                            when (it) {
                                MatchStatus.NOT_STARTED -> {
                                    val (date, time) = LiveDateUtil.getDisplay(matchBean.basicInfo.startTime)
                                    _titleText.value = time
                                    _titleTextColor.value = arch.cayenne.lib.common.R.color.white
                                    _titleTextSize.value = arch.cayenne.lib.common.R.dimen.sp_17
                                    _subTitleText.value = date
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
                                        MatchStatus.FINISHED -> R.string.match_finished.getString()
                                        MatchStatus.POSTPONED -> R.string.match_postponed.getString()
                                        MatchStatus.INTERRUPTED -> R.string.match_interrupted.getString()
                                        MatchStatus.CANCELED -> R.string.match_cancelled.getString()
                                        MatchStatus.DELAYED -> R.string.match_delayed.getString()
                                        MatchStatus.ABANDONED -> R.string.match_abandoned.getString()
                                        MatchStatus.PAUSED -> R.string.match_suspended.getString()
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


}