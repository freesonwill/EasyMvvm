package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.InfoBean
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
class CollectListViewModel : BaseMatchViewModel<CollectListRepository>() {
    override val repository : CollectListRepository by inject()
    private val balanceRepository: BalanceRepository by inject()
    val currentBalanceChange by lazy { MutableLiveData<InfoBean>() }

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

    override fun clearCurrentMatch() {
        repository.clearCurrentMatch()
    }

    override fun getMatchListData() {
        viewModelScope.launch(Dispatchers.IO) {
            "取得收藏賽事 $page".logi()
            isPageEnd = !repository.getCollectData(page)
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
        _state.value = Event(MatchListState.FIRST_LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeMatchChange().collect { ref ->
                if (ref.isEmpty()) {
                    getMatchListData()
                    return@collect
                }
                val currentRefs = ref.values.toList().sortedBy { it.order }
                page = currentRefs.maxOfOrNull { it.page } ?: 1
                //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
                val list = repository.queryFullMatches(
                    currentRefs.map { it.matchId }
                )
                withContext(Dispatchers.Main) {
                    matchListChange.value = list
                }
            }
        }
    }


    fun removeMatchCollect(item: MatchWithMarkets) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeMatchCollect(item)
        }
    }
}
