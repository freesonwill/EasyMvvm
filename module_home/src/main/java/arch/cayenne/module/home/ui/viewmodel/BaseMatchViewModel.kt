package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.MatchListState
import arch.cayenne.module.home.data.repo.BaseMatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject

abstract class BaseMatchViewModel<REPO: BaseMatchRepository> : BaseViewModel() {

    protected abstract fun getMatchListData()

    protected abstract val repository: REPO

    private val betRepository: BetRepository by inject()

    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }

    protected var page = 1
    protected var isPageEnd = false
    private val subscribeMatchSet by lazy { HashSet<Long>() }

    protected val _state  = MutableLiveData(Event(MatchListState.INIT))
    val state : LiveData<Event<MatchListState>> = _state

    private var matchNotifyJob: Job? = null

    private val refreshAllBet = MutableStateFlow(Unit)

    override fun initViewModel() {
        super.initViewModel()
        startMatchSubscribeNotify()
        //觀察投注單的變化，主要用來做selection變更
        viewModelScope.launch(Dispatchers.IO) {
            betRepository.observerAllBet
                .distinctUntilChanged()
                .combine(refreshAllBet){ beans, _ -> beans }
                .collect { betSelectionBeans ->
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeLoginChange()
                .filter { it }
                .collect {
                    if (_state.value?.peekContent() == MatchListState.FAILED) {
                        getMatchListData()
                    } else {
                        launch(Dispatchers.Main) {
                            subscribeMatch(getCurrentSubscribeMatchSet())
                        }
                    }

                }
        }
    }

    /**
     * 因為一點擊下去就會先高亮投注選項，所以如果遇到失敗等等問題，要再強迫observerAllBet重來一次，把目前有點擊的選項更新一次
     * */
    fun triggerAllBetRefresh() {
        refreshAllBet.value = Unit
    }

    fun loadNextPage() {
        if (isPageEnd) {
            _state.value = Event(MatchListState.NO_MORE_DATA)
            return
        }
        if (_state.value?.peekContent() != MatchListState.IDLE) {
            return
        }
        page++
        _state.value = Event(MatchListState.LOADING_NEXT)
        setState(HomeState.Match.LoadingNext)
        getMatchListData()
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
        if (matchListChange.value != null && subscribe.isNotEmpty()) {
            "訂閱比賽  $subscribe".logi(this::class.java.name)
            callApi({
                repository.subscribeMatch(subscribe.toList())
            }, { state ->
                if (state is ApiResponseState.Succeeded<*>) {
                    val matchWithMarkets = state.dataAs<List<MatchWithMarkets>>() ?: return@callApi
                    val old = matchListChange.value!!.toMutableList()

                    val missing = subscribe - matchWithMarkets.map { it.match.matchId }.toSet()
                    old.removeIf { missing.contains(it.match.matchId) }

                    matchWithMarkets.forEach { matchWithMarket ->
                        val index =
                            old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                        if (index != -1) {
                            old[index] = matchWithMarket
                        }
                    }
                    matchListChange.value = old
                }
            })
        }
    }

    fun cancelSubscribeMatch(cancel: Set<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (cancel.isNotEmpty()) {
                "取消訂閱比賽  $cancel".logi(this::class.java.name)
                repository.cancelSubscribeMatch(cancel.toList())
            }
        }
    }

    // 觀察賽事訂閱後，後端主動送出的變化
    fun startMatchSubscribeNotify() {
        matchNotifyJob?.cancel()
        matchNotifyJob = viewModelScope.launch(Dispatchers.IO) {
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
    }

    fun stopMatchSubscribeNotify() {
        matchNotifyJob?.cancel()
        matchNotifyJob = null
    }

    fun getCurrentSubscribeMatchSet() = subscribeMatchSet

    /**
     * selection點擊行為，投注或取消投注
     * */
    suspend fun setSelection(selectionId: Long) : AddSelectionStatus {
        if (!betRepository.isConnected) {
            return AddSelectionStatus.Failure.NetworkDisconnected
        }
        val bean = repository.getSelectionInsertBean(selectionId)
        return if (bean == null) {
            AddSelectionStatus.Failure.Fail
        } else {
            betRepository.setSelection(bean)
        }
    }

    fun updateMatchLiveData() {
        val ids = matchListChange.value?.filter { it.match.basicInfo.status == 5 && it.match.liveInfo.rollClock }?.map { it.match.matchId }?.toList() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val matchWithMarkets = repository.updateLiveMatch(ids)
            if (matchWithMarkets.isEmpty()) return@launch
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

    fun reload() {
        isPageEnd = false
        page = 1
        val preState = apiStateListener.value
        setState(HomeState.Match.Refreshing)
        viewModelScope.launch(Dispatchers.IO) {
            clearCurrentMatch()
            if (preState == HomeState.Match.DataEmpty || preState == DataState.NetworkUnavailable) {
                getMatchListData()
            }
        }
    }

    abstract fun clearCurrentMatch()
}