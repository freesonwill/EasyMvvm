package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.repo.HomeRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.Dispatchers
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

    private var currentPlayType: PlayType = PlayType.TODAY
    private var currentSportId: Int = 0
    val currentBalanceChange by lazy { MutableLiveData<Long>() }

    val sportsStatistical by lazy { MutableLiveData<Event<List<SportDataModel>>>() }

    val tournaments by lazy { MutableLiveData<Event<List<TournamentDataModel>>>() } // 今日/早盤

    private val _selectedDate = MutableLiveData<Event<Long>>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<Event<Long>> = _selectedDate

    private val _selectedTournamentId = MutableLiveData<Event<Int>>()
    val selectedTournamentId: MutableLiveData<Event<Int>> = _selectedTournamentId

    private val _collapseTournamentDropdown = MutableLiveData<Event<Boolean>>()
    val collapseTournamentDropdown: MutableLiveData<Event<Boolean>> = _collapseTournamentDropdown

    private val _navigateToChampion = MutableLiveData<Event<ChampionTournamentDataModel>>()
    val navigationToChampion: LiveData<Event<ChampionTournamentDataModel>> = _navigateToChampion

    private val _state = MutableLiveData<Event<HomeState>>()
    val state: LiveData<Event<HomeState>> = _state

    private var _recently31MatchScheduleCount = MutableLiveData<Event<List<Common.DailyMatchCount>>>()
    val recently31MatchScheduleCount: LiveData<Event<List<Common.DailyMatchCount>>> = _recently31MatchScheduleCount

    fun requestCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(true)
    }

    fun consumeCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(false)
    }

    private fun addNewTournament(id: Int) {

        val currentList = tournaments.value?.peekContent().orEmpty()
        val existsInCurrent = currentList.any { it.id == id }
        if (existsInCurrent) {
            _selectedTournamentId.postValue(Event(id))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val tournament = repository.getTournamentById(id)
                if (tournament != null) {
                    val updatedList = currentList
                        .filterNot { it.id == TOURNAMENT_ALL_ID || it.id == tournament.id }
                        .toMutableList()
                        .apply { add(tournament) }

                    val fullList =
                        listOf(TournamentDataModel.createAllItem(currentSportId)) + updatedList

                    withContext(Dispatchers.Main) {
                        tournaments.value = Event(fullList)
                        _selectedTournamentId.postValue(Event(id))
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
            _navigateToChampion.value = Event(tournament)

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

    fun refreshAll() {
        setCurrentPlayType(currentPlayType)
    }

    //切換當前的一級選項(今日、早盤、冠軍)
    fun setCurrentPlayType(playType: PlayType) {
        currentPlayType = playType
        _state.value = Event(HomeState.PLAY_TYPE_CLICK)
    }

    fun resetLiveData() {
        sportsStatistical.value = Event(arrayListOf())
        tournaments.value = Event(arrayListOf())
    }

    fun getCurrentPlayType() = currentPlayType
    fun getCurrentSportStatistical() {
        _state.value = Event(HomeState.LOADING_SPORT)
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getSportStatistical()?.filter {
                SportType.fromId(it.id) != null  //去除目前沒有在code預設內的運動
            }
            withContext(Dispatchers.Main) {
                if (list == null) {
                    //TODO 拿取sport錯誤
                    "Get Sport List failed!!".loge(this@HomeViewModel::class.java.simpleName)
                    _state.value = Event(HomeState.FAILED)
                } else if (list.isEmpty()) {
                    _state.value = Event(HomeState.NO_DATA)
                } else {
                    sportsStatistical.value = Event(list)
                    setCurrentSport(list.first().id)
                }
            }
        }

    }

    //切換當前的二級選項(各項運動)
    fun setCurrentSport(sportId: Int) {
        currentSportId = sportId
        _state.value = Event(HomeState.SPORT_LOAD_SUCCESS)
    }

    fun getCurrentSportId() = currentSportId

    fun getCurrentTournament() {
        _state.value = Event(HomeState.LOADING_TOURNAMENT)
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getTenTournaments(currentPlayType.id, currentSportId)
            "getCurrentTournament list: $list".logd()
            withContext(Dispatchers.Main) {
                if (list == null) {
                    //TODO 拿取聯賽錯誤
                    "Get Tournament List failed!!".loge(this::class.java.simpleName)
                    _state.value = Event(HomeState.FAILED)
                } else if (list.isEmpty()) {
                    _state.value = Event(HomeState.NO_DATA)
                } else {
                    tournaments.value = Event(
                        ArrayList<TournamentDataModel>().apply {
                            add(TournamentDataModel.createAllItem(currentSportId))
                            addAll(list)
                        }
                    )
                    _state.value = Event(HomeState.LOAD_TOURNAMENT_SUCCESS)
                }
            }
        }
    }

    fun setSelectedDate(date: Long) {
        if (_selectedDate.value?.peekContent() == date) return
        _selectedDate.value = Event(date)
    }

    //
    fun isLoadingMatch(b: Boolean) {
        if (b) {
            _state.value = Event(HomeState.LOADING_MATCH)
        } else {
            _state.value = Event(HomeState.LOADING_MATCH_SUCCESS)
        }
    }

    //提供子fragment透過shared HomeViewModel來告知HomeFragment該fragment的狀態
    fun changeState(state: HomeState) {
        _state.value = Event(state)
    }
    
    //获取近31日比赛日程count
    fun getRecently31MatchScheduleCount(tournamentId: Int = TOURNAMENT_ALL_ID) {
        _state.value = Event(HomeState.LOADING_RECENTLY_31_SCHEDULE)
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getRecently31MatchScheduleCount(currentSportId, currentPlayType.id,tournamentId)
            withContext(Dispatchers.Main) {
                if (list.isNotEmpty()) {
                    _recently31MatchScheduleCount.value = Event(list)
                    _state.value = Event(HomeState.LOADING_RECENTLY_31_SCHEDULE_SUCCESS)
                }
            }
        }
    }
}