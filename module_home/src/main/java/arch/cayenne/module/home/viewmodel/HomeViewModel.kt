package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.SportCategory
import arch.cayenne.lib.database.entity.TournamentCategory
import arch.cayenne.module.home.data.PlayType
import arch.cayenne.module.home.data.SportDataModel
import arch.cayenne.module.home.enums.HomeTab
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class HomeViewModel : BaseViewModel() {
    private val repository : HomeRepository by inject { parametersOf(viewModelScope) }
    private var currentPlayType : HomeTab = HomeTab.TODAY
    var currentSport: Int? = null
        private set
    var currentTournament = HashMap<Int, Int>()//(sportId, currentTournament)

    val sportsStatistical by lazy { MutableLiveData<List<SportDataModel>>() }
    val tournaments by lazy { MutableLiveData<List<TournamentCategory>>() }

    //切換當前的一級選項(今日、早盤、冠軍)
    fun setCurrentPlayType(homeTab: HomeTab) {
        currentPlayType = homeTab
        getCurrentSportStatistical()
    }

    fun getCurrentSportStatistical() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getSportStatistical(currentPlayType.id)?.filter {
                SportType.fromId(it.sportId) != null
            }?.map {
                SportDataModel(
                    id = it.sportId,
                    matchCount = it.matchCount,
                    order = it.sportOrder
                )
            }
            "!!!!!!!KC_ ${list.toString()}".loge("KC_")
            if (list.isNullOrEmpty()) {
                //TODO 拿取sport錯誤
                "Get Sport List failed!!".loge(this@HomeViewModel::class.java.simpleName)
            } else {
                withContext(Dispatchers.Main) {
                    sportsStatistical.value = list
                }
            }
        }

    }
    //切換當前的二級選項(各項運動)
    fun setCurrentSport(sportId: Int) {
        currentSport = sportId
    }

    //取得聯賽資料
    fun getCurrentTournament() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAllTournaments(currentPlayType.id, currentSport!!)
            if (list.isNullOrEmpty()) {
                //TODO 拿取聯賽錯誤
                "Get Tournament List failed!!".loge(this@HomeViewModel::class.java.simpleName)
            } else {
                withContext(Dispatchers.Main) {
                    tournaments.value = list
                }
            }
        }
    }

    //取得比賽列表
    fun getCurrentMatch() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAllMatch(currentPlayType.id, currentSport!!, currentTournament[currentSport]!!)

        }
    }

    //切換當前的三級選項(各項聯賽)
    fun setCurrentTournament(sportId: Int, tournamentId: Int) {
        currentTournament[sportId] = tournamentId
    }

}