package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.TournamentMatchRef
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.repo.BaseMatchRepository
import arch.cayenne.module.home.data.repo.BaseMatchRepository.Companion.DEFAULT_MATCH_SIZE
import arch.cayenne.module.home.data.repo.MatchListRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class MatchListViewModel : BaseMatchViewModel<MatchListRepository>() {
    private var _sportId = SportType.Init.id
    private var _playType = PlayType.TODAY.id
    private var _tournamentId: Int = HomeViewModel.TOURNAMENT_ALL_ID
    private var _position = -1
    private var _selectedDate = MutableStateFlow<Long>(0L)
    override val repository: MatchListRepository by inject()

    private var observeJob: Job? = null

    var requestScrollToTop: Boolean = false  //是否需要回到頂部，通常用於網路重新連接後，資料整體重新拉取後使用
        private set

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

    fun setSelectedDate(date: Long = 0) {
        page = 1
        _selectedDate.value = date
    }

    fun setPosition(position: Int) {
        _position = position
    }

    fun getPlayTypeId(): Int = _playType

    fun getTournamentId() = _tournamentId

    fun getSportId() = _sportId

    fun changeState(state: DataState) {
        setState(state)
    }

    fun resetRequestScrollToTop() {
        requestScrollToTop = false
    }

    fun startObserveMatch() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            //當日期變化
            launch(Dispatchers.IO) {
                _selectedDate
                    .drop(2)  //一開始進入的不用聽，可以藉由loginChange去取得最開始的資料
                    .collect { selectedDate ->
                        "Collect selectedDateChange playType = $_playType  tournament = $_tournamentId selectedDate = $selectedDate ".logi()
                        val currentDateRefs = repository.queryMatchChange(_playType, _tournamentId)
                            .filter { it.date == selectedDate }
                        if (currentDateRefs.isEmpty()) {
                            getMatchListData(LoadMatchType.DATE_CHANGE)
                            return@collect
                        }
                        processObserveMatchList(currentDateRefs)
                    }
            }
            //當內部資料有變化
            launch(Dispatchers.IO) {
                repository.observeMatchChange(_playType, _tournamentId)
                    .distinctUntilChanged()
                    .collect { refs ->
                        val selectedDate = _selectedDate.value
                        "Collect observeMatchChange start playType = $_playType, sportId = ${_sportId} tournament = $_tournamentId selectedDate = $selectedDate".logi(
                            this@MatchListViewModel::class.java.simpleName
                        )
                        val currentDateRefs = refs.filter { it.date == selectedDate }
                        if (currentDateRefs.isEmpty()) {
                            "Collect observeMatchChange TournamentMatchRef is NULL!!".logi(this@MatchListViewModel::class.java.simpleName)
                            return@collect
                        }
                        processObserveMatchList(currentDateRefs)
                    }
            }

        }
    }

    private suspend fun processObserveMatchList(currentDateRefs: List<TournamentMatchRef>) {
        //一次拿到當前頁面全部資料，會超過一頁，所以需要重新看一下page
        page = currentDateRefs.maxOfOrNull { it.page } ?: 0
        //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
        val list = repository.queryFullMatches(
            currentDateRefs.map { it.matchId }
        )
        "Collect observeMatchChange result：${list.map { it.match.matchId }}".logi(this@MatchListViewModel::class.java.simpleName)
        withContext(Dispatchers.Main) {
            //第一次http拿到的資料量過少，會影響到拉取更新資料需要等待，所以跟api補上拿取更多一點的資料
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

    //取得分頁的比賽列表
    override fun getMatchListData(loadMatchType: LoadMatchType) {
        viewModelScope.launch {
            requestScrollToTop =
                loadMatchType == LoadMatchType.RELOAD || loadMatchType == LoadMatchType.RETRY // 是否是強制更新，會刪除原本的資料ref關聯表，並且更新列表後會滾到頂端
            setState(HomeState.Match.Loading)
            val (startTime, endTime) =
                if (_playType == PlayType.EARLY.id) { //早盘
                    Pair(
                        _selectedDate.value,
                        _selectedDate.value + BaseMatchRepository.ONE_DAY_TIME_STAMP * 31
                    )
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
                        isForce = requestScrollToTop,
                    )
                },
                {
                    if (it is ApiResponseState.Failed) {
                        if (loadMatchType == LoadMatchType.NEXT_PAGE) {
                            setState(HomeState.Match.LoadNextFailure)
                            matchListChange.value = matchListChange.value
                        } else {
                            setState(DataState.NetworkUnavailable)
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
                }, autoUpdateState = false
            )
        }
    }

    suspend fun addMatchCollect(item: MatchWithMarkets, collect: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            val resp = repository.matchCollect(item, collect)
            if (resp is ApiResponseState.Succeeded<*>) {
                val matchWithMarket = resp.dataAs<MatchWithMarkets>() ?: return@withContext false
                val old = matchListChange.value!!.toMutableList()
                val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                if (index != -1) {
                    old[index] = matchWithMarket
                }
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