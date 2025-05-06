package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.enums.SportType
import arch.cayenne.module.home.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class HomeViewModel : BaseViewModel() {
    companion object {
        const val TOURNAMENT_ALL_ID = 0
    }
    private val repository : HomeRepository by inject { parametersOf(viewModelScope) }
    private val betRepository: BetRepository by inject()
    private var currentPlayType : PlayType = PlayType.TODAY
    private var currentSportId: Int = 0
    val currentBalanceChange by lazy { MutableLiveData<Long>() }

    val sportsStatistical by lazy { MutableLiveData<List<SportDataModel>>() }

    val tournaments by lazy { MutableLiveData<List<TournamentDataModel>>() }

    private val _selectedDate = MutableLiveData<String>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<String> = _selectedDate
    override fun initViewModel() {
        super.initViewModel()
        //觀察餘額變化
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }

    }

    //切換當前的一級選項(今日、早盤、冠軍)
    fun setCurrentPlayType(playType: PlayType) {
        currentPlayType = playType
        getCurrentSportStatistical()
    }

    fun getCurrentPlayType() = currentPlayType

    private fun getCurrentSportStatistical() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getSportStatistical()?.filter {
                SportType.fromId(it.id) != null  //去除目前沒有在code預設內的運動
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
        currentSportId = sportId
        getCurrentTournament(sportId)
    }

    private fun getCurrentTournament(sportId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getTenTournaments(currentPlayType.id, sportId)
            if (list.isNullOrEmpty()) {
                //TODO 拿取聯賽錯誤
                "Get Tournament List failed!!".loge(this::class.java.simpleName)
            } else {
                withContext(Dispatchers.Main) {
                    tournaments.value = ArrayList<TournamentDataModel>().apply {
                        add(TournamentDataModel.createAllItem(sportId))
                        addAll(list)
                    }
                }
            }
        }
    }

    fun setSelection(matchId: Long, selectionId: Long) {
        viewModelScope.launch {
            val status = betRepository.setSelection(matchId, selectionId)
        }
    }

    fun setSelectedDate(date: String) {
        if (_selectedDate.value == date) return
        _selectedDate.value = date
    }
}