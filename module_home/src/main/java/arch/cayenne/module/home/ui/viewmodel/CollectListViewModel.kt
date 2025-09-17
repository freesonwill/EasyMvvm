package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.repo.BaseMatchRepository
import arch.cayenne.module.home.data.repo.CollectListRepository
import galaxy.common.proto.Common
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
    val currentBalanceChange by lazy { MutableLiveData<InfoBean?>() }

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeInfo().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
    }

    override fun clearCurrentMatch() {
        repository.clearCurrentMatch()
    }

    override fun getMatchListData(loadMatchType: LoadMatchType) {
        viewModelScope.launch {
            "取得收藏賽事 $page".logi()
            setState(HomeState.Match.Loading)
            callApi({
                repository.getCollectData(
                    page = page,
                    isForce = loadMatchType == LoadMatchType.RELOAD
                )
            }, {
                if (it is ApiResponseState.Failed) {
                    if (loadMatchType == LoadMatchType.NEXT_PAGE) {
                        setState(HomeState.Match.LoadNextFailure)
                        matchListChange.value = matchListChange.value
                    } else {
                        setState(DataState.NetworkUnavailable)
                        matchListChange.value = arrayListOf()
                    }
                } else if (it is ApiResponseState.Succeeded<*>) {
                    val size = it.dataAs<List<Common.Match>>()?.size ?: 0
                    if (page == 1 && size == 0) {
                        setState(HomeState.Match.DataEmpty)
                        matchListChange.value = arrayListOf()
                    } else if (size < BaseMatchRepository.DEFAULT_MATCH_SIZE) {
                        setState(DataState.NoMoreData)
                    } else {
                        setState(HomeState.Match.LoadSuccess)
                    }
                }
            })
        }
    }

    fun startObserveMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeMatchChange().collect { ref ->
                if (ref.isEmpty()) {
                    getMatchListData(LoadMatchType.FIRST_LOAD)
                    return@collect
                }
                val currentRefs = ref.values.toList().sortedBy { it.order }
                page = currentRefs.maxOfOrNull { it.page } ?: 1
                //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
                val list = repository.queryFullMatches(
                    currentRefs.map { it.matchId }
                )
                withContext(Dispatchers.Main) {
                    setState(HomeState.Match.LoadSuccess)
                    matchListChange.value = list
                }
            }
        }
    }


    suspend fun removeMatchCollect(item: MatchWithMarkets) = withContext(Dispatchers.IO) {
        return@withContext repository.removeMatchCollect(item)
    }
}
