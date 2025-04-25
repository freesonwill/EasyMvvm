package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
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
        const val DEFAULT_MATCH_SIZE = 10
    }

    private var _sportId = SportType.Init.id
    abstract val playType: PlayType
    private var _tournamentId: Int = TOURNAMENT_ALL_ID
    val repository: HomeRepository by inject { parametersOf(viewModelScope) }

    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }

    fun setSportId(id: Int) {
        _sportId = id
    }

    fun setTournamentId(id: Int) {
        _tournamentId = id
    }

    fun getTournamentId() = _tournamentId

    //取得比賽列表
    fun getCurrentMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAllMatch(playType.id, _sportId, _tournamentId, DEFAULT_MATCH_SIZE, 1)
            if (list.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    matchListChange.value = list
                }
            }

        }
    }
}