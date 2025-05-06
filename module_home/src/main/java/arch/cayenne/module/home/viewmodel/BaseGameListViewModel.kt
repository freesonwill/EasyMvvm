package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
import arch.cayenne.module.home.viewmodel.HomeViewModel.Companion.TOURNAMENT_ALL_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class BaseGameListViewModel: BaseViewModel() {
    companion object {
        const val DEFAULT_MATCH_SIZE = 3
    }

    private var _sportId = SportType.Init.id
    private var _playType = PlayType.TODAY.id
    private var _tournamentId: Int = TOURNAMENT_ALL_ID
    var page: Int = 1
    private var isLoadingData = false
    private val subscribeMatchSet by lazy { HashSet<Long>() }
    private val repository: HomeRepository by inject { parametersOf(viewModelScope) }
    private val betRepository: BetRepository by inject { parametersOf(viewModelScope) }

    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }
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

    fun getPlayTypeId(): Int = _playType

    fun getTournamentId() = _tournamentId

    fun loadNextPage() {
        if (isLoadingData) return
        page++
        getCurrentMatch()
    }

    //取得分頁的比賽列表
    fun getCurrentMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingData = true
            "取得比賽資料  PlayType = $_playType sportId = $_sportId tornamentId = $_tournamentId page = $page startTime = 0".logi(this::class.java.name)
            val list = repository.getAllMatch(_playType, _sportId, _tournamentId, page, 0)
            if (list.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    matchListChange.value = if (matchListChange.value?.isNotEmpty() == true) {
                        matchListChange.value!! + list
                    } else {
                        list
                    }
                }
            }
            isLoadingData = false
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
}