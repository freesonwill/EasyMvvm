package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
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
    companion object {
        const val DEFAULT_MATCH_SIZE = 3
    }

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
    val isLoadingData by lazy { MutableLiveData<Boolean>() }
    override fun initViewModel() {
        super.initViewModel()
        // 觀察賽事訂閱後，後端主動送出的變化
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeMatchNotify().collect { matchWithMarket ->
                if (matchListChange.value == null) return@collect
                val old = matchListChange.value!!.toMutableList()
                val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                if (index != -1) { old[index] = matchWithMarket }
                withContext(Dispatchers.Main) {
                    matchListChange.value = old
                }
            }
        }
        //觀察投注單的變化，主要用來做selection變更
        viewModelScope.launch(Dispatchers.IO) {
            betRepository.observerAllBet.distinctUntilChanged().collect { betSelectionBeans ->
                if (matchListChange.value == null) return@collect
                val origin = matchListChange.value!!.toMutableList()
                val allSelections = origin.flatMap { it.markets }.flatMap { it.selections }  //把所有內部的selection展開
                val betSelectionSet = betSelectionBeans.map { it.selectionId }.toSet()
                allSelections.forEach { selectionBean ->
                    //當在目前注單中，但是沒有選取，或是不在目前的注單中，但是卻選取中的match，重新再從DB同步一次
                    if ((betSelectionSet.contains(selectionBean.selectionId) && !selectionBean.isSelected)
                        || (!betSelectionSet.contains(selectionBean.selectionId) && selectionBean.isSelected)) {
                        val matchWithMarket = repository.getOneMatchById(selectionBean.matchId)
                        matchWithMarket?.apply {
                            val index = origin.indexOfFirst { it.match.matchId == this.match.matchId }
                            if (index != -1) { origin[index] = this }
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    matchListChange.value = origin
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
                    isLoadingData.value = false
                }
            }
        }
    }

    fun loadNextPage() {
        if (isLoadingData.value == true || isPageEnd) return
        page++
        getCurrentMatch()
    }

    //取得分頁的比賽列表
    private fun getCurrentMatch() {
        viewModelScope.launch {
            isLoadingData.value = true
            withContext(Dispatchers.IO) {
                "取得比賽資料  PlayType = $_playType sportId = $_sportId tornamentId = $_tournamentId page = $page startTime = $_selectedDate".logi(this::class.java.name)
                isPageEnd = !repository.getAllMatch(_position, _playType, _sportId, _tournamentId, page, _selectedDate.value)
                if (isPageEnd && page == 1) {

                }
            }
            isLoadingData.value = false
        }
    }

    fun subscribeMatch(ids: Set<Long>) {
        val subscribe = ids - subscribeMatchSet
        val cancel = subscribeMatchSet - ids
        subscribeMatchSet.clear()
        subscribeMatchSet.addAll(ids)
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                "取消訂閱比賽  $cancel".logi(this::class.java.name)
                if (cancel.isNotEmpty()) {
                    repository.cancelSubscribeMatch(cancel.toList())
                }
            }
            launch {
                "訂閱比賽  $subscribe".logi(this::class.java.name)
                if (matchListChange.value != null && subscribe.isNotEmpty()) {
                    val matchWithMarkets = repository.subscribeMatch(subscribe.toList())
                    val old = matchListChange.value!!.toMutableList()
                    matchWithMarkets.forEach { matchWithMarket ->
                        val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                        if (index != -1) { old[index] = matchWithMarket }
                    }
                    withContext(Dispatchers.Main) {
                        matchListChange.value = old
                    }
                }
            }
        }
    }

    suspend fun setSelection(matchId: Long, selectionId: Long) : AddSelectionStatus {
        return betRepository.setSelection(matchId, selectionId)
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearCurrentMatch(_playType, _tournamentId, _selectedDate.value)
            getCurrentMatch()
        }
    }
}