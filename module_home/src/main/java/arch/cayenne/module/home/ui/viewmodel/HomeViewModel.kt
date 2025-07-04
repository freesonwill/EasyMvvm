package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.repo.HomeRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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

    private val repository: HomeRepository by inject()
    private val balanceRepository: BalanceRepository by inject()
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private var currentPlayType: PlayType = PlayType.TODAY
    private var currentSportId: Int = 0
    val currentBalanceChange by lazy { MutableLiveData<InfoBean>() }

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

    private var _recently31MatchScheduleCount = MutableLiveData<Event<List<Common.DailyMatchCount>>>()
    val recently31MatchScheduleCount: LiveData<Event<List<Common.DailyMatchCount>>> = _recently31MatchScheduleCount

    private var timerJob: Job? = null
    private val _timer = MutableLiveData<Event<Long>>()
    val timer: LiveData<Event<Long>> = _timer

    private val _isHomeLoading = MutableLiveData(false)
    val isHomeLoading: MutableLiveData<Boolean> get() = _isHomeLoading
    private val _selectedSkinType = MutableLiveData<Event<String>>()
    val selectedSkinType: LiveData<Event<String>> = _selectedSkinType
    private var tournamentJob: Job? = null

    init {
        viewModelScope.launch {
            repository.observeSportsMatchCount().collect {
                sportsStatistical.value = Event(it)
                if (it.isNotEmpty()) {
                    setCurrentSport(it.first().id)
                }
            }
        }
    }

    fun setIsHomeLoading(isLoading: Boolean) {
        _isHomeLoading.value = isLoading
    }

    fun requestCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(true)
    }

    fun consumeCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(false)
    }

    private fun addNewTournament(tournament: TournamentDataModel) {

        val currentList = tournaments.value?.peekContent().orEmpty()
        val existsInCurrent = currentList.any { it.id == tournament.id }
        if (existsInCurrent) {
            _selectedTournamentId.postValue(Event(tournament.id))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val updatedList = currentList
                    .filterNot { it.id == TOURNAMENT_ALL_ID || it.id == tournament.id }
                    .toMutableList()
                    .apply { add(tournament) }

                val fullList =
                    listOf(TournamentDataModel.createAllItem(currentSportId)) + updatedList

                withContext(Dispatchers.Main) {
                    tournaments.value = Event(fullList)
                    _selectedTournamentId.postValue(Event(tournament.id))
                }
            }
        }
    }

    fun onTournamentListSelected(tournament: BaseTournamentData) {
        if (tournament is TournamentDataModel) {
            addNewTournament(tournament)
        } else if (tournament is ChampionTournamentDataModel) {
            _navigateToChampion.value = Event(tournament)

        }
    }

    override fun initViewModel() {
        super.initViewModel()
        "HomeViewModel initViewModel".logd(this::class.java.simpleName)
        //觀察餘額變化
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            //觀察換肤type
            skinManager.skinFlow.collect {
                withContext(Dispatchers.Main) {
                    _selectedSkinType.value = Event(it)
                }
            }
        }
        startTimer()
    }

    fun refreshAll() {
        setCurrentPlayType(currentPlayType)
    }

    //切換當前的一級選項(今日、早盤、冠軍)
    fun setCurrentPlayType(playType: PlayType) {
        currentPlayType = playType
        setState(HomeState.PlayTypeClick)
    }

    fun getCurrentPlayType() = currentPlayType

    fun getCurrentSportStatistical() {
        setState(HomeState.Sport.Loading)
        callApi({
            repository.getSportStatistical()
        })
    }

    //切換當前的二級選項(各項運動)
    fun setCurrentSport(sportId: Int) {
        currentSportId = sportId
        setState(HomeState.Sport.LoadSuccess)
    }

    fun getCurrentSportId() = currentSportId

    fun getCurrentTournament() {
        setState(HomeState.Tournament.Loading)
        tournamentJob?.cancel()
        // TODO 之後需移到init做監聽
        tournamentJob = viewModelScope.launch {
            repository.observeTenTournaments(currentPlayType.id, currentSportId).collect {
                val data = mutableListOf<TournamentDataModel>()
                if (it.isEmpty()) {
                    callApi({
                        repository.getTenTournaments(currentPlayType.id, currentSportId)
                    }, {
                        if (it is ApiResponseState.Succeeded<*>) {
                            setState(HomeState.Tournament.LoadSuccess)
                        }
                    })
                    return@collect
                }
                data.add(TournamentDataModel.createAllItem(currentSportId))
                data.addAll(it)
                tournaments.value = Event(data)
            }
        }

    }

    fun resetSelectedDate() {
        _selectedDate.value = Event(0L)
    }

    fun setSelectedDate(date: Long) {
        if (_selectedDate.value?.peekContent() == date) return
        _selectedDate.value = Event(date)
    }

    //
    fun isLoadingMatch(b: Boolean) {
        if (b) {
            setState(HomeState.Match.Loading)
        } else {
            setState(HomeState.Match.LoadSuccess)
        }
    }

    //提供子fragment透過shared HomeViewModel來告知HomeFragment該fragment的狀態
    fun changeState(state: HomeState) {
        setState(state)
    }
    
    //获取近31日比赛日程count
    fun getRecently31MatchScheduleCount(tournamentId: Int = TOURNAMENT_ALL_ID) {
        setState(HomeState.Schedule.Loading)
        callApi({
            repository.getRecently31MatchScheduleCount(currentSportId, currentPlayType.id, tournamentId)
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                val data: List<Common.DailyMatchCount>? = it.dataAs()
                if (!data.isNullOrEmpty()) {
                    _recently31MatchScheduleCount.value = Event(data)
                    setState(HomeState.Schedule.LoadSuccess)
                }
            }
        }, autoUpdateState = false)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            var count = 0L
            while (isActive) {
                delay(1000L)
                withContext(Dispatchers.Main) {
                    _timer.value = Event(count++)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}