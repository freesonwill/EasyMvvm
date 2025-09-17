package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.repo.BaseMatchRepository
import arch.cayenne.module.home.data.repo.BaseMatchRepository.Companion.DEFAULT_MATCH_SIZE
import arch.cayenne.module.home.data.repo.MatchListRepository
import arch.cayenne.module.home.utils.DateUtils
import galaxy.common.proto.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel
import java.util.Locale

@KoinViewModel
class MatchListViewModel : BaseMatchViewModel<MatchListRepository>() {
    private var _sportId = SportType.Init.id
    private var _playType = PlayType.TODAY.id
    private var _tournamentId: Int = HomeViewModel.TOURNAMENT_ALL_ID
    private var _position = -1
    private var _selectedDate = MutableStateFlow<Long>(0)
    override val repository: MatchListRepository by inject()
    // 用來判斷是否需要執行淡入淡出動畫
    private var _lastTournamentId: Int = -1
    private var _lastSelectedDate: Long = -1L

    private var observeJob : Job? = null

    fun setSportId(id: Int) {
        _sportId = id
    }

    fun setTournamentId(id: Int) {
        if (_tournamentId == id) return
        _tournamentId = id
    }

    fun setPlayTypeId(id: Int) {
        _playType = id
    }

    fun setSelectedDate(id: Long = 0) {
        page = 1
//        _state.value = Event(MatchListState.REFRESHING)
        _selectedDate.value = id
//        getCurrentMatch()
    }

    fun setPosition(position: Int) {
        _position = position
    }

    fun setLastState(tournamentId: Int, selectedDate: Long) {
        _lastTournamentId = tournamentId
        _lastSelectedDate = selectedDate
    }

    fun getPlayTypeId(): Int = _playType

    fun getTournamentId() = _tournamentId

    fun getSportId() = _sportId

    fun getSelectedDate() = _selectedDate.value

    fun getLastTournamentId() = _lastTournamentId

    fun getLastSelectedDate() = _lastSelectedDate

    fun changeState(state: DataState) {
        setState(state)
    }

    fun startObserveMatch() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch(Dispatchers.IO) {
            combine(
                _selectedDate,
                repository.observeMatchChange(_playType, _tournamentId).distinctUntilChanged()
            ) { selectedDate, refs ->
                selectedDate to refs
            }.collect { (selectedDate, refs) ->
                "Collect observeMatchChange start playType = $_playType, sportId = ${_sportId} tournament = $_tournamentId selectedDate = $selectedDate".logi(this@MatchListViewModel::class.java.simpleName)
                val currentDateRefs = refs.filter { it.date == selectedDate }
                if (currentDateRefs.isEmpty()) {
                    "Collect observeMatchChange TournamentMatchRef is NULL!  getMatchListData again!".logi(this@MatchListViewModel::class.java.simpleName)
                    if (apiStateListener.value == null) {
                        launch(Dispatchers.Main) { setState(HomeState.Match.Loading) }
                        getMatchListData(LoadMatchType.FIRST_LOAD)
                    } else {
                        getMatchListData(LoadMatchType.RELOAD)
                    }

                    return@collect
                }

                //一次拿到當前頁面全部資料，會超過一頁，所以需要重新看一下page
                page = currentDateRefs.maxOfOrNull { it.page } ?: 0
                //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
                val list = repository.queryFullMatches(
                    currentDateRefs.map { it.matchId }
                )
                "Collect observeMatchChange result：${list.map { it.match.matchId }}".logi(this@MatchListViewModel::class.java.simpleName)
                withContext(Dispatchers.Main) {
                    //第一次http拿到的資料量過少，會影響到拉取更新資料需要等待，所以跟api補上拿取更多一點的資料
                    if (apiStateListener.value == null && list.size < DEFAULT_MATCH_SIZE) {
                        loadNextPage()
                    }
                    if (page == 1 && list.isEmpty()) {
                        setState(HomeState.Match.DataEmpty)
                    } else if (list.size % DEFAULT_MATCH_SIZE != 0) {
                        setState(DataState.NoMoreData)
                    } else {
                        setState(HomeState.Match.LoadSuccess)
                    }
                    matchListChange.value = list
                }
            }
        }
    }

    //取得分頁的比賽列表
    override fun getMatchListData(loadMatchType: LoadMatchType) {
        viewModelScope.launch {
            setState(HomeState.Match.Loading)
            val (startTime, endTime) = if (_selectedDate.value == 0L) { //ALL
                if (_playType == PlayType.EARLY.id) {
                    DateUtils.getFutureDays(1, Locale.getDefault())[0].third.let {
                        Pair(it, it + BaseMatchRepository.THIRTY_DAY_TIME_STAMP)
                    }
                } else {
                    Pair(0L, 0L)
                }
            } else {
                Pair(
                    _selectedDate.value,
                    _selectedDate.value + BaseMatchRepository.ONE_DAY_TIME_STAMP
                )
            }
            "取得比賽資料 Type = ${loadMatchType} PlayType = $_playType sportId = $_sportId tournamentId = $_tournamentId page = $page startTime = $startTime endTime = $endTime".logi(
                TAG
            )
            callApi(
                {
                    repository.getAllMatch(
                        playType = _playType,
                        sportId = _sportId,
                        tournamentId = _tournamentId,
                        page = page,
                        date = _selectedDate.value,
                        startTime = startTime,
                        endTime = endTime,
                        isForce = loadMatchType == LoadMatchType.RELOAD,
                    )
                },
                {
                    if (it is ApiResponseState.Failed) {
                        if (loadMatchType == LoadMatchType.NEXT_PAGE) {
                            setState(HomeState.Match.LoadNextFailure)
                            matchListChange.value = matchListChange.value
                        } else {
                            setState(DataState.NetworkUnavailable)
                            matchListChange.value = arrayListOf()
                        }
                    } else if (it is ApiResponseState.Succeeded<*>) {
                        val size = it.dataAs<List<Common.Match>>()?.size ?: 0
                        val isEmpty = size == 0
                        if (page == 1 && isEmpty) {
                            setState(HomeState.Match.DataEmpty)
                            matchListChange.value = arrayListOf()
                        } else if (size < BaseMatchRepository.DEFAULT_MATCH_SIZE) {   //如果返回成功，但是数据size小于10，则表明列表已经加载到底部
                            setState(DataState.NoMoreData)
                        } else {
                            setState(HomeState.Match.LoadSuccess)
                        }
                    }
                },autoUpdateState = false
            )
        }
    }

    suspend fun addMatchCollect(item: MatchWithMarkets, collect: Boolean): Boolean = withContext(Dispatchers.IO) {
        val resp = repository.matchCollect(item, collect)
        if (resp is ApiResponseState.Succeeded<*>) {
            val matchWithMarket = resp.dataAs<MatchWithMarkets>() ?: return@withContext false
            val old = matchListChange.value!!.toMutableList()
            val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
            if (index != -1) { old[index] = matchWithMarket }
            withContext(Dispatchers.Main) {
                matchListChange.value = old
            }
            return@withContext true
        } else {
            return@withContext false
        }
    }

    override fun clearCurrentMatch() {
        repository.clearCurrentMatch(_playType, _tournamentId, _selectedDate.value)
    }
}