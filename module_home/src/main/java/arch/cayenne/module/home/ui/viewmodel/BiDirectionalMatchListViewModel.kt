package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.database.entity.EarlyTournamentMatchRef
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.repo.BaseMatchRepository
import arch.cayenne.module.home.data.repo.MatchListRepository
import arch.cayenne.module.home.utils.DateUtils
import galaxy.common.proto.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
/**
 * 双向比赛列表的ViewModel, 具备切换日期及向前查询能力
 */
class BiDirectionalMatchListViewModel : BaseMatchViewModel<MatchListRepository>() {
    private var _sportId = SportType.Init.id
    private var _playType = PlayType.SUPER_COMPETITION.id
    private var _tournamentIdList: List<Int> = listOf(HomeViewModel.TOURNAMENT_ALL_ID)
    private var _position = -1

    /**
     * 表示用户手动选择的时间， 用于查询比赛列表数据
     */
    private var _queryDate = MutableStateFlow<Long>(0L)

    val queryDate = _queryDate


    override val repository: MatchListRepository by inject()

    private var observeJob: Job? = null

    private var prevPage: Int = INITIAL_PAGE

    private var isPrevPageEnd: Boolean = false

    //早盘页面可能同时向前和向后查询， 因此需要新增一个api state
    private val _prevApiStateListener = MutableLiveData<DataState>()
    val prevApiStateListener: LiveData<DataState> get() = _prevApiStateListener

    var requestScrollToTop: Boolean = false  //是否需要回到頂部，通常用於網路重新連接後，資料整體重新拉取後使用
        private set

    var job: Job? = null

    fun setSportId(id: Int) {
        _sportId = id
    }

    fun setTournamentIdList(idList: List<Int>) {
        if (_tournamentIdList == idList) return
        _tournamentIdList = idList
    }

    fun resetObserver() {
        job?.cancel()

    }


    fun setPlayTypeId(id: Int) {
        _playType = id
    }

    fun setSelectedDate(date: Long = 0, force: Boolean = false) {
        page = INITIAL_PAGE
        prevPage = INITIAL_PAGE - 1
        _queryDate.value = date

        //早盘页面向前查询时，时间不能早于明天
        isPrevPageEnd = date == DateUtils.getTomorrowMidnight()
    }


    fun setPosition(position: Int) {
        _position = position
    }

    fun getPlayTypeId(): Int = _playType

    fun getTournamentIdList() = _tournamentIdList

    fun getSportId() = _sportId

    fun resetRequestScrollToTop() {
        requestScrollToTop = false
    }

    fun startObserveDate() {
        if (observeJob != null) return
        observeJob = viewModelScope.launch {
            //當日期變化
            launch(Dispatchers.IO) {
                _queryDate
                    .drop(2)  //一開始進入的不用聽，可以藉由loginChange去取得最開始的資料
                    .collect { selectedDate ->
                        "Collect selectedDateChange playType = $_playType  tournament = $_tournamentIdList selectedDate = $selectedDate ".logi()
                        val currentDateRefs = repository.queryEarlyMatchChange(_playType,_tournamentIdList)
                            .filter { it.date == selectedDate }
                        if (currentDateRefs.isEmpty()) {
                            //向后查询数据
                            getMatchListData(LoadMatchType.DATE_CHANGE)
                            return@collect
                        }
                        processObserveMatchList(currentDateRefs)
                    }
            }
        }
    }

    fun startObserveMatch() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            repository.observeEarlyMatchChange(_playType, _tournamentIdList)
                .distinctUntilChanged()
                .collect { refs ->
                    val selectedDate = _queryDate.value
                    "Collect observeMatchChange start playType = $_playType, sportId = ${_sportId} tournament = $_tournamentIdList selectedDate = $selectedDate".logi(
                        this@BiDirectionalMatchListViewModel::class.java.simpleName
                    )
                    val currentDateRefs = refs.filter { it.date == selectedDate }
                    if (currentDateRefs.isEmpty()) {
                        "Collect observeMatchChange TournamentMatchRef is NULL!!".logi(this@BiDirectionalMatchListViewModel::class.java.simpleName)
                        return@collect
                    }
                    processObserveMatchList(currentDateRefs)
                }
        }
    }


    private suspend fun processObserveMatchList(currentDateRefs: List<EarlyTournamentMatchRef>) {
        //早盘暂时没有分页的概念
        //拿到ref後藉由ref拿到這個時間段的match id，再去資料庫把這些賽史資料串起來
        val list = repository.queryFullMatches(
            currentDateRefs.map { it.matchId }
        )
        "Collect observeMatchChange result：${list.map { it.match.matchId }}".logi(this@BiDirectionalMatchListViewModel::class.java.simpleName)
        withContext(Dispatchers.Main) {
            //第一次http拿到的資料量過少，會影響到拉取更新資料需要等待，所以跟api補上拿取更多一點的資料
            //todo :早盘还没有做分页机制
            setState(HomeState.Match.LoadSuccess)

            matchListChange.value = list
        }
    }

    //取得分頁的比賽列表
    override fun getMatchListData(loadMatchType: LoadMatchType) {
        viewModelScope.launch {
            requestScrollToTop =
                loadMatchType == LoadMatchType.RELOAD || loadMatchType == LoadMatchType.RETRY || loadMatchType == LoadMatchType.DATE_CHANGE // 是否是強制更新，會刪除原本的資料ref關聯表，並且更新列表後會滾到頂端
            setState(HomeState.Match.Loading)
            val (startTime, endTime) =
                Pair(
                    _queryDate.value,
                    _queryDate.value + BaseMatchRepository.THIRTY_DAY_TIME_STAMP
                )


            "取得比賽資料 Type = $loadMatchType PlayType = $_playType sportId = $_sportId tournamentId = $_tournamentIdList page = $page prevPage = $prevPage startTime = $startTime endTime = $endTime".logi(
                TAG
            )
            callApi(
                {
                    repository.getAllMatch(
                        playType = _playType,
                        sportId = _sportId,
                        tournamentIdList = _tournamentIdList,
                        prevPage = prevPage,
                        page = page,
                        date = _queryDate.value,
                        startTime = startTime,
                        endTime = endTime,
                        isForce = requestScrollToTop,
                        loadMatchType = loadMatchType,
                    )
                },
                {
                    if (it is ApiResponseState.Failed) {
                        when (loadMatchType) {
                            LoadMatchType.NEXT_PAGE -> {
                                setState(HomeState.Match.LoadNextFailure)
                                matchListChange.value = matchListChange.value
                            }

                            else -> {
                                setState(DataState.NetworkUnavailable)
                                setPrevApiState(DataState.NetworkUnavailable)
                            }
                        }
                    } else if (it is ApiResponseState.Succeeded<*>) {

                        val size = it.dataAs<List<Common.Match>>()?.size ?: 0
                        val isEmpty = size == 0
                        if (page == INITIAL_PAGE && isEmpty) {
                            setState(HomeState.Match.DataEmpty)
                            matchListChange.value = arrayListOf()
                        } else if (size < BaseMatchRepository.DEFAULT_MATCH_SIZE) {   //如果返回成功，但是数据size小于10，则表明列表已经加载到底部
                            setState(DataState.NoMoreData)
                        } else {
                            setState(HomeState.Match.LoadSuccess)
                        }

                        //向后查询成功，且为第一页， 则自动向前查询一页
                        if (page == INITIAL_PAGE) {
                            //向前查询一页数据
                            //早盘日期为明天时，不能向前查询数据
                            if (_queryDate.value != DateUtils.getTomorrowMidnight()) {
                                loadPrevPage()
                            }
                        }

                    }
                }, autoUpdateState = false
            )
        }
    }

    //向前查询，取得分頁的比賽列表
    private fun getPrevMatchListData() {
        viewModelScope.launch {
            setState(HomeState.Match.Loading)
            val (startTime, endTime) =
                //向前查询
                Pair(
                    //早盘只能查询从明天开始的比赛， 即使向前查询，起始时间也不能早于明天凌晨
                    DateUtils.getTomorrowMidnight(),
                    _queryDate.value,
                )

            "取得比賽資料  PlayType = $_playType sportId = $_sportId tournamentId = $_tournamentIdList page = $page prevPage = $prevPage startTime = $startTime endTime = $endTime".logi(
                TAG
            )
            callApi(
                {
                    repository.getAllMatch(
                        playType = _playType,
                        sportId = _sportId,
                        tournamentIdList = _tournamentIdList,
                        prevPage = prevPage,
                        page = page,
                        date = _queryDate.value,
                        startTime = startTime,
                        endTime = endTime,
                        isForce = false,
                        loadMatchType = LoadMatchType.PREV_PAGE,
                    )
                },
                {
                    if (it is ApiResponseState.Failed) {
                        setPrevApiState(HomeState.Match.LoadPrevFailure)
                        matchListChange.value = matchListChange.value
                    } else if (it is ApiResponseState.Succeeded<*>) {
                        val size = it.dataAs<List<Common.Match>>()?.size ?: 0
                        if (size < BaseMatchRepository.DEFAULT_MATCH_SIZE) {
                            setPrevApiState(DataState.NoMoreData)
                        } else {
                            setPrevApiState(HomeState.Match.LoadSuccess)
                        }
                    }
                }, autoUpdateState = false, { setPrevApiState(it) }
            )
        }
    }

    suspend fun addMatchCollect(item: MatchWithMarkets, collect: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            val resp = repository.matchCollect(item, collect)
            if (resp is ApiResponseState.Succeeded<*>) {
                val matchWithMarket = resp.dataAs<MatchWithMarkets>() ?: return@withContext false
                val old = matchListChange.value!!.toMutableList()
                val index = old.indexOfFirst { it.match.matchId == matchWithMarket.match.matchId }
                if (index != -1) {
                    old[index] = matchWithMarket
                }
                withContext(Dispatchers.Main) {
                    matchListChange.value = old
                }
                return@withContext true
            } else {
                return@withContext false
            }
        }

    override fun clearCurrentMatch() {
        repository.clearCurrentMatch(_playType, _tournamentIdList, _queryDate.value)
    }

    fun loadPrevPage() {
        if (isPrevPageEnd || prevApiStateListener.value == DataState.Loading) {
            return
        }

        prevPage--
        setPrevApiState(DataState.Loading)
        getPrevMatchListData()
    }

    fun changePrevPageEnd(flag: Boolean) {
        isPrevPageEnd = flag
    }

    private fun setPrevApiState(state: DataState) {
        _prevApiStateListener.value = state
    }
}