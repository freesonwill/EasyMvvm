package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.data.constants.MatchListState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.repo.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class MatchListViewModel : BaseViewModel() {
    private var _sportId = SportType.Init.id
    private var _playType = PlayType.TODAY.id
    private var _tournamentId: Int = HomeViewModel.TOURNAMENT_ALL_ID
    private var _position = -1
    private var _selectedDate = MutableStateFlow<Long>(0)
    var page: Int = 1
    var isPageEnd = false
    private val subscribeMatchSet by lazy { HashSet<Long>() }
    private val repository: HomeRepository by inject { parametersOf(viewModelScope) }
    private val betRepository: BetRepository by inject { parametersOf(viewModelScope) }

    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }
    private val _state  = MutableLiveData<Event<MatchListState>>()
    val state : LiveData<Event<MatchListState>> = _state

    override fun initViewModel() {
        super.initViewModel()
        // 觀察賽事訂閱後，後端主動送出的變化
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeMatchNotify().collect { matchWithMarket ->
                if (matchListChange.value == null) return@collect
                val old = matchListChange.value!!.toMutableList()
                val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                if (index != -1) { old[index] = matchWithMarket }
//                val matchWithMarkets = repository.queryFullMatches(matchListChange.value!!.map { it.match.matchId })
                withContext(Dispatchers.Main) {
//                    matchListChange.value = matchWithMarkets
                    matchListChange.value = old
                }
            }
        }
        //觀察投注單的變化，主要用來做selection變更
        viewModelScope.launch(Dispatchers.IO) {
            betRepository.observerAllBet.distinctUntilChanged().collect { betSelectionBeans ->
                if (matchListChange.value == null) return@collect
                val matchWithMarkets = repository.queryFullMatches(
                    matchListChange.value!!.map { it.match.matchId },
                    betSelectionBeans.map { it.selectionId }
                )
                withContext(Dispatchers.Main) {
                    matchListChange.value = matchWithMarkets
                }
            }
        }
    }

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
                    getCurrentMatch()
                    return@collect
                }
                //一次拿到當前頁面全部資料，會超過一頁，所以需要重新看一下page
                page = currentDateRefs.maxOfOrNull { it.page } ?: 0
                //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
                val list = repository.queryFullMatches(
                    currentDateRefs.map { it.matchId }
                )

                withContext(Dispatchers.Main) {
                    matchListChange.value = list
                }
            }
        }
    }

    fun loadNextPage() {
        if (_state.value?.peekContent() != MatchListState.IDLE || isPageEnd) return
        page++
        _state.value = Event(MatchListState.LOADING_NEXT)
        getCurrentMatch()
    }

    //取得分頁的比賽列表
    private fun getCurrentMatch() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                "取得比賽資料  PlayType = $_playType sportId = $_sportId tornamentId = $_tournamentId page = $page startTime = ${_selectedDate.value}".logi(this::class.java.name)
                isPageEnd = !repository.getAllMatch(_position, _playType, _sportId, _tournamentId, page, _selectedDate.value)
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

    fun compareSubscribeMatch(ids: Set<Long>) {
        val subscribe = ids - subscribeMatchSet
        val cancel = subscribeMatchSet - ids
        subscribeMatchSet.clear()
        subscribeMatchSet.addAll(ids)
        cancelSubscribeMatch(cancel)
        subscribeMatch(subscribe)
    }
    fun subscribeMatch(subscribe: Set<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            "訂閱比賽  $subscribe".logi(this::class.java.name)
            if (matchListChange.value != null && subscribe.isNotEmpty()) {
                val matchWithMarkets = repository.subscribeMatch(subscribe.toList())
                val old = matchListChange.value!!.toMutableList()
                matchWithMarkets.forEach { matchWithMarket ->
                    val index =
                        old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                    if (index != -1) {
                        old[index] = matchWithMarket
                    }
                }
                withContext(Dispatchers.Main) {
                    matchListChange.value = old
                }
            }
        }
    }

    fun cancelSubscribeMatch(cancel: Set<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            "取消訂閱比賽  $cancel".logi(this::class.java.name)
            if (cancel.isNotEmpty()) {
                repository.cancelSubscribeMatch(cancel.toList())
            }
        }
    }

    fun getCurrentSubscribeMatchSet() = subscribeMatchSet

    suspend fun setSelection(matchId: Long, selectionId: Long) : AddSelectionStatus {
        val bean = repository.getSelectionInsertBean(matchId, selectionId)
        return if (bean == null) {
            AddSelectionStatus.FAIL
        } else {
            betRepository.setSelection(bean)
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

    fun reload() {
        isPageEnd = false
        page = 1
        val preState = _state.value?.peekContent()
        _state.value = Event(MatchListState.REFRESHING)
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearCurrentMatch(_playType, _tournamentId, _selectedDate.value)
            if (preState == MatchListState.FAILED) {
                getCurrentMatch()
            }
        }
    }
}