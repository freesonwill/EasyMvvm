package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.repo.BaseMatchRepository
import arch.cayenne.module.home.data.repo.BaseMatchRepository.Companion.DEFAULT_MATCH_SIZE
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
    override val repository: CollectListRepository by inject()
    private val balanceRepository: BalanceRepository by inject()
    val currentBalanceChange by lazy { MutableLiveData<InfoBean?>() }

    private var prevPage: Int = INITIAL_PAGE - 1

    private var isPrevPageEnd: Boolean = false

    //收藏页面可能同时向前和向后查询， 因此需要新增一个api state
    private val _prevApiStateListener = MutableLiveData<DataState>()
    val prevApiStateListener: LiveData<DataState> get() = _prevApiStateListener

    /**
     * 在日期栏上展示的时间
     */
    private val _displayDate = MutableLiveData<CollectDate>()

    val displayDate: MutableLiveData<CollectDate> = _displayDate

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
        if (loadMatchType == LoadMatchType.RETRY) { //由于两次加载造成了进入的时候暂无订单两次跳转
            return
        }
        viewModelScope.launch {
            setState(HomeState.Match.Loading)
            callApi({
                repository.getCollectData(
                    page = page,
                    prevPage = prevPage,
                    reverse = false,
                    isForce = loadMatchType == LoadMatchType.RELOAD || loadMatchType == LoadMatchType.RETRY
                )
            }, {
                if (it is ApiResponseState.Failed) {
                    if (loadMatchType == LoadMatchType.NEXT_PAGE) {
                        setState(HomeState.Match.LoadNextFailure)
                        matchListChange.value = matchListChange.value
                    } else {
                        setState(DataState.NetworkUnavailable)
                        setPrevApiState(DataState.NetworkUnavailable)
                        matchListChange.value = arrayListOf()
                    }
                } else if (it is ApiResponseState.Succeeded<*>) {

                    val size = it.dataAs<List<Common.Match>>()?.size ?: 0

                    if (page == INITIAL_PAGE && size == 0) {
                        matchListChange.value = arrayListOf()

                        setState(HomeState.Match.DataEmpty)
                    } else if (size < BaseMatchRepository.DEFAULT_MATCH_SIZE) {
                        setState(DataState.NoMoreData)

                    } else {
                        setState(HomeState.Match.LoadSuccess)
                    }

                    //向后查询成功，且为第一页， 则自动向前查询一页
                    if (page == INITIAL_PAGE) {
                        //向前查询一页数据
                        loadPrevPage()
                    }

                }
            })
        }
    }

    private fun getPrevMatchListData() {
        viewModelScope.launch {
            setPrevApiState(HomeState.Match.Loading)
            callApi({
                repository.getCollectData(
                    page = page,
                    prevPage = prevPage,
                    reverse = true,
                    isForce = false
                )
            }, {
                if (it is ApiResponseState.Failed) {
                    setPrevApiState(HomeState.Match.LoadPrevFailure)
                    matchListChange.value = matchListChange.value
                } else if (it is ApiResponseState.Succeeded<*>) {
                    val size = it.dataAs<List<Common.Match>>()?.size ?: 0
                    if (size < DEFAULT_MATCH_SIZE) {
                        setPrevApiState(DataState.NoMoreData)
                    } else {
                        setPrevApiState(HomeState.Match.LoadSuccess)
                    }
                }
            }, true, {
                setPrevApiState(it)
            })
        }
    }


    fun startObserveMatch() {
        repository.clear()
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeMatchChange().collect { ref ->
                if (ref.isEmpty()) {
                    repository.deleteCollectList()
                    getMatchListData(LoadMatchType.FIRST_LOAD)
                    return@collect
                }
                val currentRefs = ref.values.toList().sortedBy { it.order }
                page = currentRefs.maxOfOrNull { it.page } ?: INITIAL_PAGE
                //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
                val list = repository.queryFullMatches(
                    currentRefs.map { it.matchId }
                )
                withContext(Dispatchers.Main) {
                    if (page == INITIAL_PAGE && list.isEmpty()) {
                        setState(HomeState.Match.DataEmpty)
                    } else if (list.size % DEFAULT_MATCH_SIZE != 0) {
                        setState(DataState.NoMoreData)
                    } else {
                        setState(HomeState.Match.LoadSuccess)
                    }
                    matchListChange.value = list
                }
                repository.insertCollectList(currentRefs)
            }
        }

    }

    /**
     * 收藏实现静默加载，初始化时从缓存中获取收藏列表
     * 带api返回后重新更新收藏列表
     * */
    fun getCacheMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            val dbRef = repository.getCollectList()
            val currentRef = dbRef.sortedBy { it.order }
            page = INITIAL_PAGE
            //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
            val list = repository.queryFullMatches(currentRef.map { it.matchId })
            withContext(Dispatchers.Main) {
                setState(HomeState.Match.LoadSuccess)
                matchListChange.value = list
            }
        }
    }

    suspend fun removeMatchCollect(item: MatchWithMarkets) = withContext(Dispatchers.IO) {
        return@withContext repository.removeMatchCollect(item)
    }

    fun loadPrevPage() {
        if (isPrevPageEnd || _prevApiStateListener.value == DataState.Loading) {
            return
        }

        prevPage--
        setPrevApiState(DataState.Loading)
        getPrevMatchListData()
    }

    fun changePrevPageEnd(flag: Boolean) {
        isPrevPageEnd = flag
    }

    private fun setPrevApiState(state: DataState) {
        _prevApiStateListener.value = state
    }


    fun setDisplayDate(date: CollectDate) {
        _displayDate.value = date
    }
}

data class CollectDate(
    val dateStr: String,
    val weekdayStr: String,
    val timestamp: Long,
)
