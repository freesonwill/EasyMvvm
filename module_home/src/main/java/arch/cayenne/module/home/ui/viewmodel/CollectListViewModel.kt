package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.data.constants.MatchListState
import arch.cayenne.module.home.data.repo.CollectListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

/**
 * @author:
 * @date: 2025/5/23 上午11:32
 * @description:
 */
@KoinViewModel
class CollectListViewModel : BaseViewModel() {
    private val balanceRepository: BalanceRepository by inject()
    private val collectListRepository : CollectListRepository by inject()
    private val betRepository: BetRepository by inject()
    val currentBalanceChange by lazy { MutableLiveData<Long>() }
    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }
    private var page = 1
    private var isPageEnd = false
    private val subscribeMatchSet by lazy { HashSet<Long>() }

    private val _state  = MutableLiveData<Event<MatchListState>>()
    val state : LiveData<Event<MatchListState>> = _state

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
        // 觀察賽事訂閱後，後端主動送出的變化
        viewModelScope.launch(Dispatchers.IO) {
            collectListRepository.observeMatchNotify().collect { matchWithMarket ->
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
                val matchWithMarkets = collectListRepository.queryFullMatches(
                    matchListChange.value!!.map { it.match.matchId },
                    betSelectionBeans.map { it.selectionId }
                )
                withContext(Dispatchers.Main) {
                    matchListChange.value = matchWithMarkets
                }
            }
        }

    }

    private fun getCollect() {
        viewModelScope.launch(Dispatchers.IO) {
            "取得收藏賽事 $page".logi()
            isPageEnd = !collectListRepository.getCollectData(page)
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

    fun startObserveMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            collectListRepository.observeMatchChange().collect { ref ->
                if (ref.isEmpty()) {
                    getCollect()
                    return@collect
                }
                val currentRefs = ref.values.toList().sortedBy { it.order }
                page = currentRefs.maxOfOrNull { it.page } ?: 1
                //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
                val list = collectListRepository.queryFullMatches(
                    currentRefs.map { it.matchId }
                )
                withContext(Dispatchers.Main) {
                    matchListChange.value = list
                }
            }
        }
    }

    suspend fun setSelection(selectionId: Long) : AddSelectionStatus {
        val bean = collectListRepository.getSelectionInsertBean(selectionId)
        return if (bean == null) {
            AddSelectionStatus.FAIL
        } else {
            betRepository.setSelection(bean)
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
        getCollect()
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
                val matchWithMarkets = collectListRepository.subscribeMatch(subscribe.toList())
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

    fun removeMatchCollect(item: MatchWithMarkets) {
        viewModelScope.launch(Dispatchers.IO) {
            collectListRepository.removeMatchCollect(item)
//            val old = matchListChange.value!!.toMutableList()
//            matchWithMarket?.apply {
//                val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
//                if (index != -1) { old[index] = matchWithMarket }
//            }
//            withContext(Dispatchers.Main) {
//                matchListChange.value = old
//            }
        }
    }

    fun cancelSubscribeMatch(cancel: Set<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            "取消訂閱比賽  $cancel".logi(this::class.java.name)
            if (cancel.isNotEmpty()) {
                collectListRepository.cancelSubscribeMatch(cancel.toList())
            }
        }
    }

    fun getCurrentSubscribeMatchSet() = subscribeMatchSet

    fun reload() {
        isPageEnd = false
        page = 1
        val preState = _state.value?.peekContent()
        _state.value = Event(MatchListState.REFRESHING)
        viewModelScope.launch(Dispatchers.IO) {
            collectListRepository.clearCurrentMatch()
            if (preState == MatchListState.FAILED) {
                getCollect()
            }
        }
    }
}
