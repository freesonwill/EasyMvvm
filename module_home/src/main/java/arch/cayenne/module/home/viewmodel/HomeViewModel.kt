package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.SportTournamentCrossRef
import arch.cayenne.module.home.data.SportDataModel
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class HomeViewModel : BaseViewModel() {
    private val repository : HomeRepository by inject { parametersOf(viewModelScope) }
    private var currentPlayType : PlayType = PlayType.TODAY
    val currentSportChange by lazy { MutableLiveData<Int>() }

    var currentTournament = HashMap<Int, Int>()//(sportId, currentTournament)

    val sportsStatistical by lazy { MutableLiveData<List<SportDataModel>>() }

    //切換當前的一級選項(今日、早盤、冠軍)
    fun setCurrentPlayType(playType: PlayType) {
        currentPlayType = playType
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
        currentSportChange.value = sportId
    }

}