package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import arch.cayenne.lib.base.data.constants.DataState
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
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
        const val DEFAULT_DATE = -1L  //預設值，如果任何livedata收到這個預設值可以先略過要做的事情，主要拿來避免頁面生成時拿到舊的date
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
    val currentBalanceChange by lazy { MutableLiveData<InfoBean>() }

    val sportsStatistical by lazy { MutableLiveData<Event<List<SportDataModel>>>() }

    val tournaments by lazy { MutableLiveData<Event<List<TournamentDataModel>>>() } // 今日/早盤

    private val _selectedDate = MutableLiveData<Event<Long>>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<Event<Long>> = _selectedDate

    private val _collapseTournamentDropdown = MutableLiveData<Event<Boolean>>()
    val collapseTournamentDropdown: MutableLiveData<Event<Boolean>> = _collapseTournamentDropdown

    private val _navigateToChampion = MutableLiveData<Event<ChampionTournamentDataModel>>()
    val navigationToChampion: LiveData<Event<ChampionTournamentDataModel>> = _navigateToChampion

    private var _recently7DayMatchScheduleCount = MutableLiveData<Event<List<Common.DailyMatchCount>>>()
    val recently7DayMatchScheduleCount: LiveData<Event<List<Common.DailyMatchCount>>> = _recently7DayMatchScheduleCount

    private var timerJob: Job? = null
    private val _timer = MutableLiveData<Event<Long>>()
    val timer: LiveData<Event<Long>> = _timer

    //聯賽收回上滑動畫結束事件
    private val _tournamentSlideOutEnd = MutableLiveData<Event<Unit>>()
    val tournamentSlideOutEnd: LiveData<Event<Unit>> = _tournamentSlideOutEnd

    private val _notifyToChampion = MutableLiveData<Event<Unit>>()
    val notifyToChampion: LiveData<Event<Unit>> = _notifyToChampion

    fun notifyTournamentSlideOutEnd() {
        _tournamentSlideOutEnd.value = Event(Unit)
    }

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
            //當PlayType被觸發時，會等待Sport也被觸發再一起往下傳
            val playTypeTrigger = _currentPlayTypeId.flatMapLatest { playTypeId ->
                _currentSportId
                    .filter { it != SportEnum.Default.id }
                    .map { sportId -> Pair(playTypeId, sportId) }
            }
            //當Sport被觸發時，會直接往下傳送
            val sportTrigger = _currentSportId
                .filter { it != SportEnum.Default.id }
                .map { sportId -> Pair(_currentPlayTypeId.value, sportId) }
            //把上面兩個trigger merge起來，兩個觸發時可以重新拉取聯賽資料
            merge(playTypeTrigger, sportTrigger)
                .distinctUntilChanged()
                .flatMapLatest { (playTypeId, sportId) ->
                    repository.observeTenTournaments().map { list ->
                        list.filter { it.playTypeId == playTypeId && it.sportId == sportId }
                    }
                }.map {
                    it.take(10)  //limit
                }.distinctUntilChanged { old, new ->
                    if (old.size != new.size) return@distinctUntilChanged false
                    return@distinctUntilChanged old.indices.all { index ->
                        old[index].id == new[index].id
                                && old[index].playTypeId == new[index].playTypeId
                                && old[index].sportId == new[index].sportId
                    }
                }
                .collect {
                    if (currentPlayTypeId != PlayType.CHAMPION.id) {
                        val selectedTournament =
                            repository.getCurrentSelectedTournamentId(currentPlayTypeId)?.let {
                                repository.getTournament(currentPlayTypeId, currentSportId, it)
                            }
                        withContext(Dispatchers.Main) {
                            val list = mutableListOf<TournamentDataModel>()
                            if (it.isEmpty()) { return@withContext }
                            list.add(
                                TournamentDataModel.createAllItem(
                                    currentPlayTypeId,
                                    currentSportId
                                )
                            )
                            list.addAll(it)
                            if (selectedTournament == null) {
                                setCurrentTournamentId(0)
                                list.find { it.id == 0 }?.isSelected = true
                            } else if (!it.any { data -> data.id == selectedTournament.id }) {  //有在目前聯賽中，但是沒有在前10筆資料中，所以新增第11筆，並且點擊它
                                setCurrentTournamentId(selectedTournament.id)
                                selectedTournament.isSelected = true
                                list.add(selectedTournament)
                            } else {
                                list.find { it.id == selectedTournament.id }?.isSelected = true
                                setCurrentTournamentId(selectedTournament.id)
                            }
                            tournaments.value = Event(list)
                            setState(HomeState.Tournament.LoadSuccess)
                        }
                    }
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeLanguageChange().collect {
                resetAll()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeLoginChange()
                .filter { it && apiStateListener.value == DataState.NetworkUnavailable }
                .collect {
                    launch(Dispatchers.Main) {
                        setCurrentPlayType(currentPlayTypeId)
                        setCurrentSport(currentSportId)
                    }
                }
        }
    }

    fun requestCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(true)
    }

    fun consumeCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(false)
    }

    fun onTournamentListSelected(tournament: BaseTournamentData) {
        if (tournament is TournamentDataModel) {
            val currentList = tournaments.value?.peekContent() ?: return
            setCurrentTournamentId(tournament.id)
            if (!currentList.any { it.id == tournament.id }) {
                tournaments.value = Event(currentList.map { it.apply { isSelected = false } }  + tournament.apply { isSelected = true })
            } else {
                tournaments.value = Event(currentList.map { it.apply { isSelected = tournament.id == id } })
            }
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
        startTimer()
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
            } else if (it is ApiResponseState.Failed) {
                if (tournaments.value?.peekContent() == null || tournaments.value?.peekContent()?.isEmpty() == true) {
                    tournaments.value = Event(
                        arrayListOf(
                            TournamentDataModel.createAllItem(
                                currentPlayTypeId,
                                currentSportId
                            )
                        )
                    )
                }
                setState(HomeState.Sport.LoadFailure)
            }
        })
    }

    //切換當前的二級選項(各項運動)
    fun setCurrentSport(sportId: Int) {
        _selectedDate.value = Event(DEFAULT_DATE)  //先送出一個初始值，避免MatchListPage生成時會拿到舊值先拿取資料
        _currentSportId.value = sportId
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSelectedSportId(currentPlayTypeId, currentSportId)
        }
        if (currentPlayTypeId != PlayType.CHAMPION.id) {
            tournaments.value = Event(arrayListOf())
            setCurrentSelectedDate()   //目前日期跟著球類走，ex:早盤日期目前是7.11，不管點擊哪一個聯賽都是7.11資料，所以設定完當前選擇的球類後先設定日期
            getCurrentTournament()
        } else {
            _notifyToChampion.value = Event(Unit)
        }
    }

    //切換當前的三級選項(聯賽)
    fun setCurrentTournamentId(tournamentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSelectedTournamentId(currentPlayTypeId, tournamentId)
        }
    }

    fun getCurrentTournament() {
        setState(HomeState.Tournament.Loading)
        callApi({
            repository.getTenTournaments(currentPlayTypeId, currentSportId)
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                setState(HomeState.Tournament.LoadSuccess)
            } else if (it is ApiResponseState.Failed) {
                if ((tournaments.value?.peekContent() == null || tournaments.value?.peekContent()?.isEmpty() == true)) {
                    tournaments.value = Event(
                        arrayListOf(
                            TournamentDataModel.createAllItem(
                                currentPlayTypeId,
                                currentSportId
                            )
                        )
                    )
                }
                setState(HomeState.Tournament.LoadFailure)
            }
        })
    }

    private fun setCurrentSelectedDate() {
        viewModelScope.launch(Dispatchers.IO) {
            val date = repository.getCurrentSelectedDate(currentPlayTypeId.playTypeToShowType(), currentSportId) ?: 0L
            launch(Dispatchers.Main) {
                selectedDate(date)
            }
        }
    }

    suspend fun selectedDate(date: Long) {
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

    //提供子fragment透過shared HomeViewModel來告知HomeFragment該fragment的狀態
    fun changeState(state: DataState) {
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
                    _recently7DayMatchScheduleCount.value = Event(data.take(7))
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