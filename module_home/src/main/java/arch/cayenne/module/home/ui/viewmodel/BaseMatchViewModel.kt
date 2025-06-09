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
import arch.cayenne.module.home.data.repo.BaseMatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
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

    protected val _state  = MutableLiveData<Event<MatchListState>>()
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

    fun loadNextPage() {
        if (isPageEnd) {
            _state.value = Event(MatchListState.NO_MORE_DATA)
            return
        }
        if (_state.value?.peekContent() != MatchListState.IDLE) return
        page++
        _state.value = Event(MatchListState.LOADING_NEXT)
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

    /**
     * selection點擊行為，投注或取消投注
     * */
    suspend fun setSelection(selectionId: Long) : AddSelectionStatus {
        val bean = repository.getSelectionInsertBean(selectionId)
        return if (bean == null) {
            AddSelectionStatus.FAIL
        } else {
            betRepository.setSelection(bean)
        }
    }

    fun updateMatchLiveData() {
        val ids = matchListChange.value?.filter { it.match.basicInfo.status == 5 && it.match.liveInfo.rollClock }?.map { it.match.matchId }?.toList() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val matchWithMarkets = repository.updateLiveMatch(ids)
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
        val preState = _state.value?.peekContent()
        _state.value = Event(MatchListState.REFRESHING)
        viewModelScope.launch(Dispatchers.IO) {
            clearCurrentMatch()
            if (preState == MatchListState.FAILED) {
                getMatchListData()
            }
        }
    }

    abstract fun clearCurrentMatch()
}