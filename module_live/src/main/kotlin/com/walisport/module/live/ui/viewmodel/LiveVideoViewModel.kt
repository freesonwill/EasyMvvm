package com.walisport.module.live.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.data.MuteManager
import com.walisport.module.live.data.repository.LiveVideoRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveVideoViewModel(private val repo: LiveVideoRepository) : BaseViewModel() {

    val videoPlayVisible = MutableLiveData(View.VISIBLE)

    val statusVisible = MutableLiveData(View.INVISIBLE)

    val playerAUrl = MutableLiveData<String>("")

    val playerBUrl = MutableLiveData<String>("")


    val titleText = MutableLiveData<String>("")
    val titleTextColor = MutableLiveData<Int>(arch.cayenne.lib.common.R.color.white)
    val titleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_17)

    val subTitleText = MutableLiveData<String>("")
    val subTitleTextColor = MutableLiveData<Int>(arch.cayenne.lib.res.R.color.color_929298)
    val subTitleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_14)


    private val _leagueIconUrl = MutableLiveData("")
    val leagueIconUrl: LiveData<String> = _leagueIconUrl

    private val _playerAName = MutableLiveData("法国")
    val playerAName: LiveData<String> = _playerAName

    private val _playerBName = MutableLiveData("阿根廷")
    val playerBName: LiveData<String> = _playerBName

    private val _liveVideoBean = MutableLiveData<LiveVideoBean>()
    val liveVideoBean: LiveData<LiveVideoBean> get() = _liveVideoBean

    private val _muted = MutableLiveData(false)
    val muted: LiveData<Boolean> = _muted

    private val muteManager: MuteManager by inject { parametersOf() }


    fun setPlayingVideoId(id: Int) {
        _liveVideoBean.value?.source?.firstOrNull { it.id == id }?.isPlaying = true
        repo.setPlayingVideoId(_liveVideoBean.value?.source ?: emptyList())
    }

    fun changeMuteStatus() {
        viewModelScope.launch {
            muteManager.changeMuteStatus()
        }
    }

    fun mutedData() = muteManager.mutedLiveData

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


    fun queryLiveStream() {
        repo.queryLiveStream()
    }

}