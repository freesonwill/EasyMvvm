package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.data.constants.MatchListState
import arch.cayenne.module.home.data.repo.CollectListRepository
import kotlinx.coroutines.Dispatchers
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
    val currentBalanceChange by lazy { MutableLiveData<Long>() }
    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }
    private var page = 1
    private var isPageEnd = false

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

    }

    private fun getCollect() {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun loadNextPage() {
        if (_state.value?.peekContent() != MatchListState.IDLE || isPageEnd) return
        page++
        _state.value = Event(MatchListState.LOADING_NEXT)
        getCollect()
    }

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
