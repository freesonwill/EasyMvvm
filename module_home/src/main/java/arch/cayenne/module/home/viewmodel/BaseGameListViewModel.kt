package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
import arch.cayenne.module.home.viewmodel.HomeViewModel.Companion.TOURNAMENT_ALL_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class BaseGameListViewModel: BaseViewModel() {
    companion object {
        const val DEFAULT_MATCH_SIZE = 3
    }

    private var _sportId = SportType.Init.id
    abstract val playType: PlayType
    private var _tournamentId: Int = TOURNAMENT_ALL_ID
    var page: Int = 1
    private val repository: HomeRepository by inject { parametersOf(viewModelScope) }

    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }
    override fun initViewModel() {
        super.initViewModel()
        observeMatchData()

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
    }

    fun setSportId(id: Int) {
        _sportId = id
    }

    fun setTournamentId(id: Int) {
        _tournamentId = id
    }

    private fun observeMatchData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeFullMatchData(
                playType = playType.id,
                tournamentId = _tournamentId,
                page = page,
                startTime = 0
            ).collect { list ->
                //TODO 接上被動連接的資料
//                if (list.isNotEmpty()) {
//                    withContext(Dispatchers.Main) {
//                        matchListChange.value = list
//                    }
//                }
            }
        }
    }

    fun getTournamentId() = _tournamentId

    //取得分頁的比賽列表
    fun getCurrentMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            "取得比賽資料  PlayType = ${playType.id} sportId = $_sportId tornamentId = $_tournamentId page = $page startTime = 0".logi(this::class.java.name)
            val list = repository.getAllMatch(playType.id, _sportId, _tournamentId, DEFAULT_MATCH_SIZE, page, 0)
            if (list.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    matchListChange.value = if (matchListChange.value?.isNotEmpty() == true) {
                        matchListChange.value!! + list
                    } else {
                        list
                    }
                }
                //測試
//                launch {
//                    if (page <= 3) {
//                        delay(3000)
//                        page++
//                        getCurrentMatch()
//                    }
//                }
                //TODO 測試訂閱遊戲
                if (page == 1) {
                    subscribeMatch(matchListChange.value!!.map { it.match.matchId })
                }
            }

        }
    }

    fun subscribeMatch(ids: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            "訂閱比賽  $ids".logi(this::class.java.name)
            val matchWithMarkets = repository.subscribeMatch(ids)

            if (matchListChange.value == null) return@launch
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