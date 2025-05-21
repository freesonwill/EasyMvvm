package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.repo.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class HomeViewModel : BaseViewModel() {
    companion object {
        const val TOURNAMENT_ALL_ID = 0
    }

    private val repository: HomeRepository by inject()
    private val balanceRepository: BalanceRepository by inject()
    var gameListPageIndex = 0
    private val betRepository: BetRepository by inject()
    private var currentPlayType: PlayType = PlayType.TODAY
    private var currentSportId: Int = 0
    val currentBalanceChange by lazy { MutableLiveData<Long>() }

    val sportsStatistical by lazy { MutableLiveData<List<SportDataModel>>() }

    val tournaments by lazy { MutableLiveData<List<TournamentDataModel>>() } // 今日/早盤

    private val _selectedDate = MutableLiveData<Long>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<Long> = _selectedDate

    private val _selectedTournamentId = MutableLiveData<Int>()
    val selectedTournamentId: MutableLiveData<Int> = _selectedTournamentId

    private val _collapseTournamentDropdown = MutableLiveData<Boolean>()
    val collapseTournamentDropdown: MutableLiveData<Boolean> = _collapseTournamentDropdown
    private val _appendTournament = MutableLiveData<TournamentDataModel?>()
    val appendTournament: MutableLiveData<TournamentDataModel?> = _appendTournament

    //用SharedFlow處理掉返回後livedata會重複接收問題
    private val _navigateToChampion = MutableSharedFlow<ChampionTournamentDataModel>(replay = 0, extraBufferCapacity = 0)
    val navigationToChampion: Flow<ChampionTournamentDataModel> = _navigateToChampion
//    private val _navigateToChampion = MutableLiveData<ChampionTournamentDataModel?>()
//    val navigationToChampion: LiveData<ChampionTournamentDataModel?> = _navigateToChampion

    fun requestCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = true
    }

    fun consumeCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = false
    }

    private fun addNewTournament(id: Int) {
        val currentList = tournaments.value.orEmpty()
        val existsInCurrent = currentList.any { it.id == id }
        if (existsInCurrent) {
            _selectedTournamentId.postValue(id)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val tournament = repository.getTournamentById(id)
                if (tournament != null) {
                    val updatedList = currentList
                        .filterNot { it.id == TOURNAMENT_ALL_ID }
                        .toMutableList()
                        .apply { add(tournament) }

                    val fullList = listOf(TournamentDataModel.createAllItem(currentSportId)) +
                            updatedList.distinctBy { it.id }
                    withContext(Dispatchers.Main) {
                        tournaments.value = fullList
                        _appendTournament.value = tournament
                        _selectedTournamentId.postValue(id)
                    }
                } else {
                    "Tournament ID:$id not found".loge(this::class.java.simpleName)
                }
            }
        }
    }

    fun onTournamentListSelected(tournament: BaseTournamentData) {
        if (tournament is TournamentDataModel) {
            addNewTournament(tournament.id)
        } else if (tournament is ChampionTournamentDataModel) {
            viewModelScope.launch {
                _navigateToChampion.emit(tournament)
            }
//            _navigateToChampion.value = tournament

        }
    }

    override fun initViewModel() {
        super.initViewModel()
        //觀察餘額變化
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeBalance().collect {
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

    fun resetLiveData() {
        sportsStatistical.value = arrayListOf()
        tournaments.value = arrayListOf()
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
        if (currentPlayType != PlayType.CHAMPION) {
            getCurrentTournament(sportId)
        }

    }

    fun getCurrentSportId() = currentSportId

    private fun getCurrentTournament(sportId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getTenTournaments(currentPlayType.id, sportId)
            "getCurrentTournament list: $list".logd()
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

    fun setSelectedDate(date: Long) {
        if (_selectedDate.value == date) return
        _selectedDate.value = date
    }

//    fun resetNavigationToChampion() {
//        _navigateToChampion.value = null
//    }


}