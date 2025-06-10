package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketConnectState
import com.walisport.module.live.data.LiveMainRepository
import com.walisport.module.live.data.model.Incidents
import com.walisport.module.live.data.model.MatchHalfTeamStats
import com.walisport.module.live.data.model.MatchLiveData
import com.walisport.module.live.data.model.MatchTrendData
import com.walisport.module.live.data.model.Stat
import com.walisport.module.live.data.repository.LiveChatRepository
import galaxy.client.proto.Sloth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import plugin.koin.KoinViewModel

@KoinViewModel
class LiveMainViewModel(
    private val repo: LiveMainRepository,
    private val chatRepo: LiveChatRepository
) : BaseViewModel() {

    //比赛ID
    private val _matchId = MutableLiveData<Long>(0)
    val matchId: LiveData<Long> = _matchId

    private val _sportId = MutableLiveData<Int>(0)
    val sportId: LiveData<Int> = _sportId

    //联赛ID
    private val _leagueID = MutableLiveData<Int>(0)
    val leagueID: LiveData<Int> = _leagueID

    //联赛名称
    private val _leagueName = MutableLiveData("")
    val leagueName: LiveData<String> = _leagueName

    //联赛Logo
    private val _leagueLogo = MutableLiveData("")
    val leagueLogo: LiveData<String> = _leagueLogo

    //首次加载
    private val _mainMatch = MutableLiveData<LiveMatchBean>()
    val mainMatch: LiveData<LiveMatchBean> = _mainMatch

    //技术统计
    private val _statisticData = MutableLiveData<MatchLiveData>()
    val statisticData: LiveData<MatchLiveData> = _statisticData

    //监听数据变化
    private val _observeMainMatch = MutableLiveData<LiveMatchBean>()
    val observeMainMatch: LiveData<LiveMatchBean> = _observeMainMatch
    val currentBalanceChange by lazy { MutableLiveData<Long>() }

    //监听matchId和sportId，并设置1s的防抖
    @OptIn(FlowPreview::class)
    val matchIdSportIdObserver: Flow<Pair<Long, Int>> =
        matchId.asFlow().combine(sportId.asFlow()) { matchId, sportId -> matchId to sportId }
            .debounce(1000)

    override fun initViewModel() {
        super.initViewModel()
        //监听余额变化
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
    }

    fun setMatchId(matchId: Long) {
        _matchId.value = matchId
    }

    fun setSportId(sportId: Int) {
        _sportId.value = sportId
    }

    fun setLeagueID(leagueID: Int) {
        _leagueID.value = leagueID
    }

    fun setLeagueName(leagueName: String) {
        _leagueName.value = leagueName
    }

    fun setLeagueLogo(leagueLogo: String) {
        _leagueLogo.value = leagueLogo
    }

    fun getMainMatch(matchId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getMatchRes(matchId) {
                _mainMatch.value = it
            }
        }
    }

    fun observeMatchBean(matchId: Long) {
        viewModelScope.launch {
            repo.observeMatchBean(matchId).collect {
                _observeMainMatch.value = it
            }
        }
    }

    fun registerMatchInfoNotify(matchId: Long) {
        viewModelScope.launch {
            repo.registerMatchInfoNotify(matchId)
            repo.observeMatchInfoNotify()
        }
    }

    fun unregisterMatchInfoNotify(matchId: Long) {
        viewModelScope.launch {
            repo.unregisterMatchInfoNotify(matchId)
        }
    }

    fun clearAllMatch() {
        viewModelScope.launch {
            repo.clearAllMatch()
        }
    }

    //订阅比赛技术统计推送
    fun registerStatisticsNotify(matchId: Long) {
        viewModelScope.launch {
            val resp = repo.registerMatchStaticsNotify(matchId)
            val temp = getMatchLiveData(resp)
            _statisticData.value = temp
        }
    }

    //取消订阅比赛技术统计推送
    fun unregisterStatisticsNotify(matchId: Long) {
        viewModelScope.launch {
            repo.unregisterStatisticsNotify(matchId)
        }
    }

    //监听比赛技术统计推送
    fun observeMatchStaticsNotify() {
        viewModelScope.launch {
            repo.observeMatchStaticsNotify().collect {
                val temp = getMatchLiveData(it)
                _statisticData.value = temp
            }
        }
    }

    private fun getMatchLiveData(data: Sloth.MatchLiveData?): MatchLiveData {
        val teams = data?.teamStatsList?.mapIndexed { _, item ->
            MatchHalfTeamStats(
                type = item.type,
                homeNum = item.homeNum,
                awayNum = item.awayNum
            )
        } ?: emptyList()
        val stats = data?.statsList?.mapIndexed { _, item ->
            Stat(
                type = item.type,
                home = item.home,
                away = item.away
            )
        } ?: emptyList()
        val incidents =
            data?.matchTrendData?.incidentsList?.mapIndexed { _, item ->
                Incidents(
                    time = item.time,
                    position = item.position,
                    type = item.type
                )
            } ?: emptyList()
        val list: MutableList<Int> = ArrayList()
        data?.matchTrendData?.dataList?.mapIndexed { _, item ->
            list.addAll(item.valuesList)
        }
        val trend = MatchTrendData(incidents, list)
        return MatchLiveData(0, teams, stats, trend)
    }

    /**
     * 开启聊天服务
     * */
    fun startChatServer() {
        viewModelScope.launch {
            val state = chatRepo.getConnectStateFlow().value
            "startChatserver $state".logd(TAG)
            if (state != SocketConnectState.None && state != SocketConnectState.Closed) {
                return@launch
            }
             chatRepo.startSocket()
        }
    }

    /**
     * 关闭聊天服务
     * */
    fun disConnectChatServer() {
        viewModelScope.launch {
            try{
                val value =  chatRepo.disconnect(viewModelScope)
                "chat disconnect viewModel $value".logd(TAG)
            }catch (e:CancellationException){
                "chat disconnect viewModel canceled".logi(TAG)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}