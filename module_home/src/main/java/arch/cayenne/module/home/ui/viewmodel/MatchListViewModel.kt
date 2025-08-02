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

    fun getPlayTypeId(): Int = _playType

    fun getTournamentId() = _tournamentId

    fun getSportId() = _sportId

    fun startObserveMatch() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            combine(
                _selectedDate,
                repository.observeMatchChange(_playType, _tournamentId).distinctUntilChanged()
            ) { selectedDate, refs ->
                selectedDate to refs
            }.collect { (selectedDate, refs) ->
                val currentDateRefs = refs.filter { it.date == selectedDate }
                if (currentDateRefs.isEmpty()) {
                    if (apiStateListener.value == null) {
                        setState(HomeState.Match.Loading)
                    }
                    getMatchListData()
                    return@collect
                }
                //一次拿到當前頁面全部資料，會超過一頁，所以需要重新看一下page
                page = currentDateRefs.maxOfOrNull { it.page } ?: 0
                //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
                val list = repository.queryFullMatches(
                    currentDateRefs.map { it.matchId }
                )

                withContext(Dispatchers.Main) {
                    setState(HomeState.Match.LoadSuccess)
                    matchListChange.value = list
                }
            }
        }
    }

    //取得分頁的比賽列表
    override fun getMatchListData() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {

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
                "取得比賽資料  PlayType = $_playType sportId = $_sportId tournamentId = $_tournamentId page = $page startTime = $startTime endTime = $endTime".logi(
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
                        )
                    },
                    {
                        if (it is ApiResponseState.Failed) {
                            matchListChange.value = arrayListOf()
                        } else if (it is ApiResponseState.Succeeded<*>) {
                            val size = it.dataAs<List<Common.Match>>()?.size ?: 0
                            val isEmpty = size == 0
                            if (page == 1 && isEmpty) {
                                matchListChange.value = arrayListOf()
                                setState(HomeState.Match.DataEmpty)
                            }
                            //如果返回成功，但是数据size小于10，则表明列表已经加载到底部
                            if (size < 10) {
                                setState(DataState.NoMoreData)
                            }
                        }
                    }
                )
            }
        }
    }

    fun addMatchCollect(item: MatchWithMarkets, collect: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val matchWithMarket = repository.matchCollect(item, collect)
            val old = matchListChange.value!!.toMutableList()
            matchWithMarket?.apply {
                val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                if (index != -1) {
                    old[index] = matchWithMarket
                }
            }
            withContext(Dispatchers.Main) {
                matchListChange.value = old
            }
        }
    }

    override fun clearCurrentMatch() {
        repository.clearCurrentMatch(_playType, _tournamentId, _selectedDate.value)
    }
}