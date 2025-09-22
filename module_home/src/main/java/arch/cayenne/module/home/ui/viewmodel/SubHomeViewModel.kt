package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.getPlayTypeById
import arch.cayenne.module.home.data.constants.playTypeToShowType
import arch.cayenne.module.home.data.repo.HomeRepository
import arch.cayenne.module.home.ui.view.HomeCalendarFragment
import galaxy.common.proto.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SubHomeViewModel: BaseViewModel() {

    private val repository: HomeRepository by inject()

    var currentPlayTypeId: Int = PlayType.TODAY.id
        private set

    val sportsStatistical by lazy { MutableLiveData<Event<List<SportDataModel>>>() }

    private val _currentSportId: MutableStateFlow<Int> = MutableStateFlow(SportEnum.Default.id)
    val currentSportId: Int
        get() = _currentSportId.value

    private val _currentSportIdChange =  MutableLiveData<Event<Int>>()
    val currentSportIdChange: LiveData<Event<Int>> = _currentSportIdChange

    private val _selectedDate = MutableLiveData<Event<Long>>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<Event<Long>> = _selectedDate

    val tournaments by lazy { MutableLiveData<Event<List<TournamentDataModel>>>() } // 今日/早盤

    private val _calendarStates = MutableLiveData<HomeCalendarFragment.States>()
    val calendarStates = _calendarStates

    private var _recently7DayMatchScheduleCount = MutableLiveData<Event<List<Common.DailyMatchCount>>>()
    val recently7DayMatchScheduleCount: LiveData<Event<List<Common.DailyMatchCount>>> = _recently7DayMatchScheduleCount

    //聯賽收回上滑動畫結束事件
    private val _tournamentSlideOutEnd = MutableLiveData<Event<Unit>>()
    val tournamentSlideOutEnd: LiveData<Event<Unit>> = _tournamentSlideOutEnd

    private val _collapseTournamentDropdown = MutableLiveData<Event<Boolean>>()
    val collapseTournamentDropdown: MutableLiveData<Event<Boolean>> = _collapseTournamentDropdown

    private val _navigateToChampion = MutableLiveData<Event<ChampionTournamentDataModel>>()
    val navigationToChampion: LiveData<Event<ChampionTournamentDataModel>> = _navigateToChampion
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val _selectedSkinType = MutableLiveData<Event<String>>()
    val selectedSkinType: LiveData<Event<String>> = _selectedSkinType

    private var _pageSelectedTimestamp: Long = 0L

    // 用於記錄全部的比賽列表是否載入完成
    var isAllTabLoaded: Boolean = false
    // 暫存 SportDataModel 列表，用於實現延後繪製球種列表
    var tempSportData: List<SportDataModel>? = null

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            skinManager.skinFlow.collect {
                _selectedSkinType.value = Event(it)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeSportsMatchCount()
                .map {
                    it.filter { it.type == currentPlayTypeId.playTypeToShowType() }
                }
                .distinctUntilChanged { old, new ->
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
                    if (selectedSportId == null || !list.any {data ->  data.id == selectedSportId }) {  //从DB找不到目前点击的sport
                        val bean = list.find {data ->  data.matchCount > 0 } ?: list.first()
                        setCurrentSport(bean.id)
                        bean.isSelected = true
                    } else {
                        setCurrentSport(selectedSportId)
                        list.firstOrNull {data -> data.id == selectedSportId }?.isSelected = true
                    }
                    withContext(Dispatchers.Main.immediate) {
                        sportsStatistical.value = Event(list)
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            //當Sport被觸發時，會直接往下傳送
            val sportTrigger = _currentSportId
                .filter { it != SportEnum.Default.id }
            //把上面兩個trigger merge起來，兩個觸發時可以重新拉取聯賽資料
            sportTrigger
                .distinctUntilChanged()
                .flatMapLatest { sportId ->
                    repository.observeTenTournaments().map { list ->
                        list.filter { it.playTypeId == currentPlayTypeId && it.sportId == sportId }
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
                    "联赛资料  collect ".logi(this@SubHomeViewModel::class.java.simpleName)
                    if (currentPlayTypeId != PlayType.CHAMPION.id) {
                        val selectedTournament =
                            repository.getCurrentSelectedTournamentId(currentPlayTypeId)?.let {
                                repository.getTournament(currentPlayTypeId, currentSportId, it)
                            }
                        val list = mutableListOf<TournamentDataModel>()
                        if (it.isEmpty()) {
                            return@collect
                        }
                        list.add(
                            TournamentDataModel.createAllItem(
                                currentPlayTypeId,
                                currentSportId
                            )
                        )
                        list.addAll(it)
                        if (selectedTournament == null) {
                            "联赛资料 setCurrentTournamentId(0) ".logi(this@SubHomeViewModel::class.java.simpleName)
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
                        launch(Dispatchers.Main) {
                            "送出联赛资料到UI".logi(this@SubHomeViewModel::class.java.simpleName)
                            tournaments.value = Event(list)
                            setState(HomeState.Tournament.LoadSuccess)
                        }
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _currentSportId.collect {
                launch(Dispatchers.Main) {
                    _currentSportIdChange.value = Event(it)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.observeLoginChange()
                .filter {
                    it && (!repository.isPreloadSuccess()
                            || apiStateListener.value == DataState.NetworkUnavailable
                            || apiStateListener.value == HomeState.Sport.LoadFailure
                            || apiStateListener.value == HomeState.Tournament.LoadFailure)
                }
                .collect {
                    launch(Dispatchers.Main) {
                        getCurrentSportStatistical()
                        getCurrentTournament()
                    }
                }
        }

        _pageSelectedTimestamp = System.currentTimeMillis()
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
                    "Get Sport List Failure set only all into tournaments livedata".loge(this::class.java.simpleName)
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
        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.Main) {
                _selectedDate.value = Event(HomeViewModel.DEFAULT_DATE)  //先送出一個初始值，避免MatchListPage生成時會拿到舊值先拿取資料
            }
            _currentSportId.value = sportId
            repository.updateSelectedSportId(currentPlayTypeId, currentSportId)
            if (currentPlayTypeId != PlayType.CHAMPION.id) {
                "On setCurrentSport -> Clear Tournaments LiveData & Update Tournaments from API".logi(this::class.java.simpleName)
                setCurrentSelectedDate()   //目前日期跟著球類走，ex:早盤日期目前是7.11，不管點擊哪一個聯賽都是7.11資料，所以設定完當前選擇的球類後先設定日期
                getCurrentTournament()
            }
        }
    }

    fun getCurrentTournament() {
        viewModelScope.launch {
            if (currentSportId == -1) return@launch
            setState(HomeState.Tournament.Loading)
            callApi({
                repository.getTenTournaments(currentPlayTypeId, currentSportId)
            }, {
                if (it is ApiResponseState.Succeeded<*>) {
                    "联赛资料 存入DB完成 ".logi(this@SubHomeViewModel::class.java.simpleName)
                    setState(HomeState.Tournament.LoadSuccess)
                } else if (it is ApiResponseState.Failed) {
                    if ((tournaments.value?.peekContent() == null || tournaments.value?.peekContent()?.isEmpty() == true)) {
                        "Get Tournament List Failure set only all into tournaments livedata".loge(this::class.java.simpleName)
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
    }

    private suspend fun setCurrentSelectedDate() {
        val date = repository.getCurrentSelectedDate(currentPlayTypeId.playTypeToShowType(), currentSportId) ?: 0L
        withContext(Dispatchers.Main) {
            selectedDate(date)
        }
    }

    suspend fun selectedDate(date: Long) {
        if (_selectedDate.value?.peekContent() == date) return
        withContext(Dispatchers.IO) {
            repository.updateSelectedDate(currentPlayTypeId.playTypeToShowType(), currentSportId, date)
        }
        _selectedDate.value = Event(date)
    }

    fun setCurrentTournamentId(tournamentId: Int) {
        viewModelScope.launch {
            repository.updateSelectedTournamentId(currentPlayTypeId, tournamentId)
        }
    }

    //获取近31日比赛日程count
    fun getRecently31MatchScheduleCount(tournamentId: Int = HomeViewModel.TOURNAMENT_ALL_ID) {
        setState(HomeState.Schedule.Loading)
        callApi({
            repository.getRecently31MatchScheduleCount(currentSportId, currentPlayTypeId, tournamentId)
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                val data: List<Common.DailyMatchCount>? = it.dataAs()
                if (!data.isNullOrEmpty()) {
                    _recently7DayMatchScheduleCount.value = Event(data)
                    setState(HomeState.Schedule.LoadSuccess)
                }
            }
        }, autoUpdateState = false)
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

    fun requestCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(true)
    }

    fun consumeCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = Event(false)
    }

    fun notifyTournamentSlideOutEnd() {
        _tournamentSlideOutEnd.value = Event(Unit)
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

    fun setPlayTypeId(id: Int) {
        currentPlayTypeId = id
    }

    fun setCalendarState(state: HomeCalendarFragment.States) {
        _calendarStates.value = state
    }

    fun getPageSelectedTimestamp() = _pageSelectedTimestamp

    /**
     * @return 是否超過時間，需要重新整理賽事資料
     * */
    fun resetPageSelectedTimestamp(): Boolean {

        val refreshInternal = currentPlayTypeId.getPlayTypeById().refreshInterval
        val res = _pageSelectedTimestamp != 0L && System.currentTimeMillis() - _pageSelectedTimestamp > refreshInternal
        "KC__ currentPlayTypeId = ${currentPlayTypeId}  res = $res  pageSelectedTimestamp = ${_pageSelectedTimestamp}".logi()
        _pageSelectedTimestamp = System.currentTimeMillis()
        return res
    }
}