package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.SportEnum
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
import arch.cayenne.module.home.data.constants.playTypeToShowType
import arch.cayenne.module.home.data.repo.HomeRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
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

    private val _currentPlayTypeId: MutableStateFlow<Int> = MutableStateFlow(PlayType.TODAY.id)
    val currentPlayTypeId: Int
        get() = _currentPlayTypeId.value
    val playTypeIndexChange: LiveData<Event<Int>> = _currentPlayTypeId.transform {
        when(it) {
            PlayType.TODAY.id -> emit(Event(0))
            PlayType.EARLY.id -> emit(Event(1))
            PlayType.CHAMPION.id -> emit(Event(2))
            else -> Unit
        }
    }.asLiveData(Dispatchers.Main)

    private val _currentSportId: MutableStateFlow<Int> = MutableStateFlow(SportEnum.Default.id)
    val currentSportId: Int
        get() = _currentSportId.value

    private val repository: HomeRepository by inject()
    private val balanceRepository: BalanceRepository by inject()
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
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

    private val _selectedSkinType = MutableLiveData<Event<String>>()
    val selectedSkinType: LiveData<Event<String>> = _selectedSkinType

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeSportsMatchCount()
                .combine(_currentPlayTypeId) { list, playTypeId ->
                    list.filter { it.type == playTypeId.playTypeToShowType() }
                }.distinctUntilChanged { old, new ->
                    if (old.size != new.size) return@distinctUntilChanged false
                    return@distinctUntilChanged old.indices.all { index ->
                        old[index].id == new[index].id
                            && old[index].matchCount == new[index].matchCount
                            && old[index].order == new[index].order
                    }
                }.collect { list ->
                    if (list.isEmpty()) {
                        return@collect
                    }
                    val selectedSportId = repository.getCurrentSelectedSportId(currentPlayTypeId)
                    launch(Dispatchers.Main) {
                        if (selectedSportId == null || !list.any {data ->  data.id == selectedSportId }) {  //从DB找不到目前点击的sport
                            val bean = list.find {data ->  data.matchCount > 0 } ?: list.first()
                            setCurrentSport(bean.id)
                            bean.isSelected = true
                        } else {
                            setCurrentSport(selectedSportId)
                            list.firstOrNull {data -> data.id == selectedSportId }?.isSelected = true
                        }
                        sportsStatistical.value = Event(list)
                    }
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeTenTournaments()
                .combine(_currentPlayTypeId) { list, playTypeId ->
                    list.filter { it.playTypeId == playTypeId }
                }.combine(_currentSportId.filter { it != SportEnum.Default.id }) { list, sportId ->
                    list.filter { it.sportId == sportId }
                }.map {
                    it.take(10)  //limit
                }.distinctUntilChanged()
                .collect {
                    val selectedTournament = repository.getCurrentSelectedTournamentId(currentPlayTypeId)?.let {
                        repository.getTournament(currentPlayTypeId, currentSportId, it)
                    }

                    withContext(Dispatchers.Main) {
                        val list = mutableListOf<TournamentDataModel>()
                        if (it.isEmpty()) {
                            return@withContext
                        }
                        list.add(TournamentDataModel.createAllItem(currentPlayTypeId, currentSportId))
                        list.addAll(it)

                        tournaments.value = Event(list)
                        if (selectedTournament == null) {
                            setCurrentTournamentId(0)
                        } else if (!it.any {data -> data.id == selectedTournament.id}) {  //有在目前聯賽中，但是沒有在前10筆資料中，所以新增第11筆，並且點擊它
                            addNewTournament(selectedTournament)
                        } else {
                            setCurrentTournamentId(selectedTournament.id)
                        }
                    }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeLanguageChange().collect {
                resetAll()
            }
        }
    }

    fun requestCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(true)
    }

    fun consumeCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(false)
    }

    private fun addNewTournament(tournament: TournamentDataModel) {
        viewModelScope.launch(Dispatchers.Main) {
            val currentList = tournaments.value?.peekContent().orEmpty()
            val existsInCurrent = currentList.any { it.id == tournament.id }
            if (!existsInCurrent) {
                val fullList = withContext(Dispatchers.IO) {
                    val updatedList = currentList
                        .filterNot { it.id == TOURNAMENT_ALL_ID || it.id == tournament.id }
                        .toMutableList()
                        .apply { add(tournament) }
                    listOf(TournamentDataModel.createAllItem(currentPlayTypeId, currentSportId)) + updatedList
                }
                tournaments.value = Event(fullList)
            }
            setCurrentTournamentId(tournament.id)
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
        setCurrentPlayType(currentPlayTypeId)
    }

    @Transaction
    private suspend fun resetAll() {
        repository.clearAllCache()
        withContext(Dispatchers.Main) {
            setCurrentPlayType(PlayType.TODAY.id)
        }
    }

    //切換當前的一級選項(今日、早盤、冠軍)
    fun setCurrentPlayType(playTypeId: Int) {
        _currentPlayTypeId.value = playTypeId
        setState(HomeState.PlayTypeClick)
        getCurrentSportStatistical()
    }

    fun getCurrentSportStatistical() {
        setState(HomeState.Sport.Loading)
        callApi({
            repository.getSportStatistical()
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                setState(HomeState.Sport.LoadSuccess)
            }
        })
    }

    //切換當前的二級選項(各項運動)
    fun setCurrentSport(sportId: Int) {
        _currentSportId.value = sportId
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSelectedSportId(currentPlayTypeId, currentSportId)
        }
        getCurrentTournament()
    }

    //切換當前的三級選項(聯賽)
    fun setCurrentTournamentId(tournamentId: Int) {
        _selectedTournamentId.postValue(Event(tournamentId))
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSelectedTournamentId(currentPlayTypeId, tournamentId)
        }

        viewModelScope.launch(Dispatchers.IO) {
            val date = repository.getCurrentSelectedDate(currentPlayTypeId.playTypeToShowType(), currentSportId) ?: 0L
            launch(Dispatchers.Main) {
                setSelectedDate(date)
            }
        }
    }

    fun getCurrentTournament() {
        setState(HomeState.Tournament.Loading)
        callApi({
            repository.getTenTournaments(currentPlayTypeId, currentSportId)
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                setState(HomeState.Tournament.LoadSuccess)
            }
        })
    }

    suspend fun setSelectedDate(date: Long) {

        if (_selectedDate.value?.peekContent() == date) return
        withContext(Dispatchers.IO) {
            repository.updateSelectedDate(currentPlayTypeId.playTypeToShowType(), currentSportId, date)
        }
        _selectedDate.value = Event(date)
    }

    fun updateCoordinate(
        playTypeId: Int,
        sportId: Int,
        tournamentId: Int,
        coordinate: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateScrollCoordinate(playTypeId, sportId, tournamentId, coordinate)
        }
    }

    suspend fun getCurrentPageCoordinate(
        playTypeId: Int,
        sportId: Int,
        tournamentId: Int,
    ): Int {
        return withContext(Dispatchers.IO) {
            repository.getCurrentPageCoordinate(playTypeId, sportId, tournamentId) ?: 0
        }
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
            repository.getRecently31MatchScheduleCount(currentSportId, currentPlayTypeId, tournamentId)
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