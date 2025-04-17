package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

abstract class BasePlayTypeViewModel : BaseViewModel() {
    companion object{
        const val TOURNAMENT_ALL_ID = 0
    }
    protected var currentSportId = SportType.Init.id
    abstract val playType: PlayType
    protected val repository : HomeRepository by inject { parametersOf(viewModelScope) }

    val tournaments by lazy { MutableLiveData<List<TournamentDataModel>>() }

    //取得聯賽資料
    fun getCurrentTournament(sportId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getTenTournaments(playType.id, sportId)
            if (list.isNullOrEmpty()) {
                //TODO 拿取聯賽錯誤
                "Get Tournament List failed!!".loge(this@BasePlayTypeViewModel::class.java.simpleName)
            } else {
                withContext(Dispatchers.Main) {
                    tournaments.value = ArrayList<TournamentDataModel>().apply {
                        add(TournamentDataModel.createAllItem())
                        addAll(list)
                    }
                }
            }
        }
    }

    fun setCurrentSport(id: Int) {
        currentSportId = id
    }
}