package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.data.constants.MatchListState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.repo.MatchListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private var _selectedDate = MutableStateFlow<Long>(0)
    private var _homeOrPullLoadingState = false
    override val repository: MatchListRepository by inject()

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
        _state.value = Event(MatchListState.REFRESHING)
        _selectedDate.value = id
//        getCurrentMatch()
    }

    fun setPosition(position: Int) {
        _position = position
    }

    fun getPlayTypeId(): Int = _playType

    fun getTournamentId() = _tournamentId

    fun getPosition() = _position

    fun startObserveMatch() {
        _state.value = Event(MatchListState.FIRST_LOADING)
        viewModelScope.launch {
            combine(
                _selectedDate,
                repository.observeMatchChange(_playType, _tournamentId).distinctUntilChanged()
            ) { selectedDate, refs ->
                selectedDate to refs
            }.collect { (selectedDate, refs) ->
                val currentDateRefs = refs.filter { it.startTime == selectedDate }
                if (currentDateRefs.isEmpty()) {
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
                    _state.value = Event(MatchListState.IDLE)
                    matchListChange.value = list
                }
            }
        }
    }

    //取得分頁的比賽列表
    override fun getMatchListData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                "取得比賽資料  PlayType = $_playType sportId = $_sportId tornamentId = $_tournamentId page = $page startTime = ${_selectedDate.value}".logi(this::class.java.name)
                isPageEnd = !repository.getAllMatch(_playType, _sportId, _tournamentId, page, _selectedDate.value)
                withContext(Dispatchers.Main) {
                    if (isPageEnd && page == 1) {
                        //沒有資料
                        _state.value = Event(MatchListState.FAILED)
                        matchListChange.value = arrayListOf()
                    } else {
                        _state.value = Event(MatchListState.IDLE)
                    }
                }
            }
        }
    }

    fun addMatchCollect(item: MatchWithMarkets, collect: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val matchWithMarket = repository.matchCollect(item, collect)
            val old = matchListChange.value!!.toMutableList()
            matchWithMarket?.apply {
                val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                if (index != -1) { old[index] = matchWithMarket }
            }
            withContext(Dispatchers.Main) {
                matchListChange.value = old
            }
        }
    }

    override fun clearCurrentMatch() {
        repository.clearCurrentMatch(_playType, _tournamentId, _selectedDate.value)
    }

    fun setHomeOrPullLoadingState(isLoading: Boolean = false) {
        _homeOrPullLoadingState = isLoading
    }

    fun showLoading() {
        if (!_homeOrPullLoadingState) {
            _state.value = Event(MatchListState.SHOW_LOADING)
        }
    }

    fun hideLoading() {
        _state.value = Event(MatchListState.HIDE_LOADING)
    }
}