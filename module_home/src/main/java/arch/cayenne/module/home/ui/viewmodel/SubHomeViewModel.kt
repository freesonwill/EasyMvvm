package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.common.utils.ext.VIPDataExt
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.module.home.TournamentCombo
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.TournamentSortType
import arch.cayenne.module.home.data.constants.playTypeToShowType
import arch.cayenne.module.home.data.repo.HomeRepository
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
open class SubHomeViewModel : BaseViewModel() {

    val repository: HomeRepository by inject()

    var currentPlayTypeId: Int = PlayType.TODAY.id
        private set

    val sportsStatistical by lazy { MutableLiveData<Event<List<SportDataModel>>>() }

    val _currentSportId: MutableStateFlow<Int> = MutableStateFlow(SportEnum.Default.id)
    val currentSportId: Int
        get() = _currentSportId.value

    private val _currentSportIdChange = MutableLiveData<Event<Int>>()
    val currentSportIdChange: LiveData<Event<Int>> = _currentSportIdChange

    val tournaments by lazy { MutableLiveData<Event<List<TournamentCombo>>>() } // 今日/早盤

    val tournamentsPlain by lazy { MutableLiveData<Event<List<TournamentDataModel>>>() } // 今日/早盤


    //聯賽收回上滑動畫結束事件
    private val _tournamentSlideOutEnd = MutableLiveData<Event<Unit>>()
    val tournamentSlideOutEnd: LiveData<Event<Unit>> = _tournamentSlideOutEnd

    private val _collapseTournamentDropdown = MutableLiveData<Event<Boolean>>()
    val collapseTournamentDropdown: MutableLiveData<Event<Boolean>> = _collapseTournamentDropdown

    protected val _navigateToChampion = MutableLiveData<Event<ChampionTournamentDataModel>>()
    val navigationToChampion: LiveData<Event<ChampionTournamentDataModel>> = _navigateToChampion
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val _selectedSkinType = MutableLiveData<Event<String>>()
    val selectedSkinType: LiveData<Event<String>> = _selectedSkinType

    // VIP 等級數據
    private val _vipLevel = MutableLiveData<Long>()
    val vipLevel: LiveData<Long> = _vipLevel

    // 用於記錄全部的比賽列表是否載入完成
    var isAllowTabLoad: Boolean = false

    // 暫存 SportDataModel 列表，用於實現延後繪製球種列表
    var tempSportData: List<SportDataModel>? = null

    private val _currentSelectedTournaments = MutableLiveData<List<Int>>(emptyList())
    val currentSelectedTournaments: LiveData<List<Int>> = _currentSelectedTournaments

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            skinManager.skinFlow.collect {
                _selectedSkinType.value = Event(it)
            }
        }

        // 監聽 VIP 等級變化
        viewModelScope.launch(Dispatchers.IO) {
            VIPDataExt.observeVIPLevel().collect { level ->
                withContext(Dispatchers.Main) {
                    _vipLevel.value = level
                }
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
                    if (selectedSportId == null || !list.any { data -> data.id == selectedSportId }) {  //从DB找不到目前点击的sport
                        val bean = list.find { data -> data.matchCount > 0 } ?: list.first()
                        setCurrentSport(bean.id)
                        bean.isSelected = true
                    } else {
                        setCurrentSport(selectedSportId)
                        list.firstOrNull { data -> data.id == selectedSportId }?.isSelected = true
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
                        val selectedTournament = it.map { tournamentDataModel ->
                            repository.getCurrentSelectedTournamentId(currentPlayTypeId)?.let {
                                repository.getTournament(
                                    currentPlayTypeId,
                                    currentSportId,
                                    tournamentDataModel.id
                                )
                            }
                        }
                        val list = mutableListOf<TournamentDataModel>()
                        list.add(
                            TournamentDataModel.createAllItem(
                                currentPlayTypeId,
                                currentSportId
                            )
                        )
                        list.addAll(it)
                        if (selectedTournament == null) {
                            "联赛资料 setCurrentTournamentId(0) ".logi(this@SubHomeViewModel::class.java.simpleName)
                            setCurrentTournamentIdList(listOf(0))
                            list.find { it.id == 0 }?.isSelected = true
                        } else if (!it.any { data ->
                                data.id in selectedTournament.filterNotNull()
                                    .map { tournamentDataModel -> tournamentDataModel.id }
                            }) {  //有在目前聯賽中，但是沒有在前10筆資料中，所以新增第11筆，並且點擊它
                            setCurrentTournamentIdList(
                                selectedTournament.filterNotNull()
                                    .map { tournamentDataModel -> tournamentDataModel.id })
                            selectedTournament.filterNotNull()
                                .forEach { model -> model.isSelected = true }
                            list.addAll(selectedTournament.filterNotNull())
                        } else {
                            list.find { tournamentDataModel ->
                                tournamentDataModel.id in selectedTournament.filterNotNull()
                                    .map { model -> model.id }
                            }?.isSelected = true

                            setCurrentTournamentIdList(
                                selectedTournament.filterNotNull()
                                    .filter { tournamentDataModel -> tournamentDataModel.isSelected }
                                    .map { tournamentDataModel -> tournamentDataModel.id })
                        }
                        launch(Dispatchers.Main) {
                            "送出联赛资料到UI".logi(this@SubHomeViewModel::class.java.simpleName)
                            tournaments.value =
                                Event(list.map { tournament -> TournamentCombo(listOf(tournament), false) })

                            tournamentsPlain.value = Event(list)
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
    }

    fun getCurrentSportStatistical() {
        setState(HomeState.Sport.Loading)
        callApi({
            repository.getSportStatistical()
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                setState(HomeState.Sport.LoadSuccess)
            } else if (it is ApiResponseState.Failed) {
                if (tournaments.value?.peekContent() == null || tournaments.value?.peekContent()
                        ?.isEmpty() == true
                ) {
                    "Get Sport List Failure set only all into tournaments livedata".loge(this::class.java.simpleName)
                    tournaments.value = Event(
                        listOf(
                            TournamentCombo(
                                arrayListOf(
                                    TournamentDataModel.createAllItem(
                                        currentPlayTypeId,
                                        currentSportId
                                    )
                                ), false
                            )
                        )
                    )
                    tournamentsPlain.value = Event(
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
    open fun setCurrentSport(sportId: Int) {
        "setCurrentSport: $sportId, playType: $currentPlayTypeId".logi("dataIssue")
        viewModelScope.launch(Dispatchers.IO) {
            _currentSportId.value = sportId
            repository.updateSelectedSportId(currentPlayTypeId, currentSportId)
            if (currentPlayTypeId != PlayType.CHAMPION.id) {
                "On setCurrentSport -> Clear Tournaments LiveData & Update Tournaments from API".logi(
                    this::class.java.simpleName
                )
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
                    if ((tournaments.value?.peekContent() == null || tournaments.value?.peekContent()
                            ?.isEmpty() == true)
                    ) {
                        "Get Tournament List Failure set only all into tournaments livedata".loge(
                            this::class.java.simpleName
                        )
                        tournaments.value = Event(
                            listOf(
                                TournamentCombo(
                                    arrayListOf(
                                        TournamentDataModel.createAllItem(
                                            currentPlayTypeId,
                                            currentSportId
                                        )
                                    ), false
                                )
                            )
                        )

                        tournamentsPlain.value = Event(
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


    fun setCurrentTournamentIdList(tournamentIdList: List<Int>) {
        viewModelScope.launch {
            repository.updateSelectedTournamentIdList(currentPlayTypeId, tournamentIdList)
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

    // 標記外部tab是否有切換（用於判斷是否需要清空彈窗的篩選結果）
    var hasTournamentTabSwitched = false

    // 保存彈窗中的選中狀態（跨彈窗生命週期）
    private val _savedTournamentSelections = MutableLiveData<List<Int>>(
        emptyList()
    )
    val savedTournamentSelections: LiveData<List<Int>> = _savedTournamentSelections

    // 通知聯賽按鈕選中狀態變化
    private val _tournamentButtonHasSelection = MutableLiveData<Event<Boolean>>()
    val tournamentButtonHasSelection: LiveData<Event<Boolean>> = _tournamentButtonHasSelection

    // 標記外部tab已切換（僅設置標記，不修改數據）
    fun markTournamentTabSwitched() {
        hasTournamentTabSwitched = true
    }

    open fun onTournamentListSelected(tournament: BaseTournamentData) {
        // 標記外部tab已切換，下次打開彈窗時需要清空篩選
        hasTournamentTabSwitched = true

        if (tournament is TournamentDataModel) {
            val currentList = tournaments.value?.peekContent() ?: return
            setCurrentTournamentIdList(listOf(tournament.id))
            currentList.forEach { it.isSelected = false }
        } else if (tournament is ChampionTournamentDataModel) {
            _navigateToChampion.value = Event(tournament)
        }
    }

    // 檢查是否切換了外部tab
    fun checkAndResetTournamentTabSwitched(): Boolean {
        val switched = hasTournamentTabSwitched
        hasTournamentTabSwitched = false
        return switched
    }

    // 重置外部tab切換標記（當用戶確認篩選後調用）
    fun resetTournamentTabSwitched() {
        hasTournamentTabSwitched = false
    }

    // 獲取已保存的選中狀態
    fun getSavedTournamentSelections(): List<Int> {
        return savedTournamentSelections.value!!
    }

    // 保存選中狀態（在確認時調用）
    fun saveTournamentSelections(selections: List<Int>) {
        _savedTournamentSelections.value = selections
        // 通知按鈕狀態更新
        _tournamentButtonHasSelection.value = Event(selections.isNotEmpty())
        // TODO: 未來同時保存到後端
    }

    // 清空保存的選中狀態
    fun clearSavedTournamentSelections() {
        _savedTournamentSelections.value = emptyList()
        // 通知按鈕狀態更新
        _tournamentButtonHasSelection.value = Event(false)
    }

    // 檢查當前是否有選中狀態
    fun hasTournamentSelections(): Boolean {
        return _savedTournamentSelections.value!!.isNotEmpty()
    }

    fun getTournamentsName(tournamentId: List<Int>): String {

        val stringList = mutableListOf<String>()
        tournamentId.forEach { id ->
            val tournamentCombo =
                tournaments.value?.peekContent()?.firstOrNull { it.containsTournament(id) }

            stringList.add(tournamentCombo?.getTournamentName(id) ?: "")
        }

        return stringList.joinToString(separator = "/")
    }

    fun getTournamentsNamePlain(tournamentId: List<Int>): String {
        val stringList = mutableListOf<String>()
        tournamentId.forEach { id ->
            val tournamentData =
                tournamentsPlain.value?.peekContent()?.firstOrNull { it.id == id }

            stringList.add(tournamentData?.simpleName ?: "")
        }

        return stringList.joinToString(separator = "/")
    }

    // 通知需要清除 tlLeagueList 的選中狀態
    private val _shouldClearLeagueListSelection = MutableLiveData<Event<Unit>>()
    val shouldClearLeagueListSelection: LiveData<Event<Unit>> = _shouldClearLeagueListSelection

    // 觸發清除 tlLeagueList 選中狀態（在彈窗確認時調用）
    fun requestClearLeagueListSelection() {
        _shouldClearLeagueListSelection.value = Event(Unit)
    }

    fun setPlayTypeId(id: Int) {
        currentPlayTypeId = id
        isAllowTabLoad = currentPlayTypeId != PlayType.TODAY.id
    }

    fun clearTournamentsSelected(){
        _currentSelectedTournaments.value = emptyList()
    }

    fun selectTournamentsId(id: Int) {
        _currentSelectedTournaments.value = listOf(id)
    }

    fun setTournaments(ints: List<Int>) {
        _currentSelectedTournaments.value = ints
    }

    fun setSortType(currentSortType: TournamentSortType) {

    }

}