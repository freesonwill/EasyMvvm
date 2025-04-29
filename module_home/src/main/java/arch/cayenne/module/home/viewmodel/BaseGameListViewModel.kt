package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
import arch.cayenne.module.home.viewmodel.HomeViewModel.Companion.TOURNAMENT_ALL_ID
import kotlinx.coroutines.Dispatchers
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
    private val repository: HomeRepository by inject { parametersOf(viewModelScope) }

    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }
    override fun initViewModel() {
        super.initViewModel()
        observeMatchData()
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

    private fun observeMatchData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeFullMatchData(
                playType = _playType,
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

    //取得比賽列表
    fun getCurrentMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            "getCurrentMatch取得首頁比賽資料  PlayType = ${_playType} sportId = $_sportId tornamentId = $_tournamentId page = $page startTime = 0".logi(
                this::class.java.name
            )
            val list = repository.getAllMatch(
                playType = _playType,
                sportId = _sportId,
                tournamentId = _tournamentId,
                size = DEFAULT_MATCH_SIZE,
                page = page,
                startTime = 0
            )
            if (list.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    matchListChange.value = list
                }
            }

        }
    }
}