package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
import arch.cayenne.module.home.viewmodel.BasePlayTypeViewModel.Companion.TOURNAMENT_ALL_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class BaseGameListViewModel: BaseViewModel() {
    companion object {
        const val DEFAULT_MATCH_SIZE = 10
    }

    protected var currentSportId = SportType.Init.id
    abstract val playType: PlayType
    protected var tournamentId: Int = TOURNAMENT_ALL_ID
    val repository: HomeRepository by inject { parametersOf(viewModelScope) }

    val matchListChange by lazy { MutableLiveData<List<MatchWithMarkets>>() }

    fun setCurrentSport(id: Int) {
        currentSportId = id
    }

    fun setCurrentTournamentId(id: Int) {
        tournamentId = id
    }

    //取得比賽列表
    fun getCurrentMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAllMatch(playType.id, currentSportId, tournamentId, DEFAULT_MATCH_SIZE, 1)
            if (list.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    matchListChange.value = list
                }
            }

        }
    }
}