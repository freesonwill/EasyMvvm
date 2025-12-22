package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.GameSortType
import arch.cayenne.lib.common.data.constants.GameSortType.*
import arch.cayenne.lib.common.ui.view.SimpleTabDataModel
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import com.haibin.calendarview.Calendar
import com.walisport.module.search.data.constants.SearchResultRaceItemType
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.data.model.SearchMatchBean
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.data.model.SearchResultTeamBean
import com.walisport.module.search.data.model.SearchResultTournamentBean
import com.walisport.module.search.data.repo.SearchRepository
import com.walisport.module.search.ui.model.Avatar
import com.walisport.module.search.ui.model.HotColdType
import com.walisport.module.search.ui.model.SearchGameContentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import java.util.Date

enum class DirectPageMode {
    SPORTS,  // 體育直配（聯賽/球隊/球員）
    VENDOR   // 遊戲供應商/分類直配
}

@KoinViewModel
class SearchResultDirectMatchViewModel: BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    /** 精準搜尋結果 */
    private val _directData = MutableStateFlow<SearchResultBaseBean?>(null)
    val directData: StateFlow<SearchResultBaseBean?> = _directData.asStateFlow()

    /** 整理後搜尋結果 賽事用 */
    private val _combineResult = MutableStateFlow<List<SearchResultRaceItemType>?>(null)
    val combineResult: StateFlow<List<SearchResultRaceItemType>?> = _combineResult.asStateFlow()

    /** 選擇的日期 */
    private val _selectedDateFlow = MutableStateFlow<Date?>(null)
    val selectedDateFlow: StateFlow<Date?> = _selectedDateFlow.asStateFlow()

    /** 精準搜尋結果類型 */
    private var _directMatchType: SearchTypeEnum? = null
    val directMatchType: SearchTypeEnum?
        get() = _directMatchType

    /** 精準搜尋結果 ID */
    private var _directMatchId: Int? = null
    val directMatchId: Int?
        get() = _directMatchId

    /** 篩選時間(開始) */
    private var _startTime: Long? = null
    val startTime: Long?
        get() = _startTime

    /** 篩選時間(結束) */
    private var _endTime: Long? = null
    val endTime: Long?
        get() = _endTime

    /** 當前頁面標題 */
    private var _currentTitle: String? = null
    val currentTitle: String?
        get() = _currentTitle

    /** 暫存有比賽的日期 */
    private var _raceDateMap: MutableMap<String, Calendar> = mutableMapOf()
    val racedDateMap: Map<String, Calendar>
        get() = _raceDateMap

    /** 監聽登入狀態變化 */
    fun observeLoginChange() = repository.observeLoginChange()

    /** 取得精準搜尋結果 */
    fun getSearchResult(data: SearchResultBean) {
        viewModelScope.launch {
            setResult(data)
            setRaceDate(data)
        }
    }

    /** 取得精準搜尋結果 */
    fun getSearchResult(
        id: String,
        type: SearchTypeEnum,
        startTime: Long? = null,
        endTime: Long? = null,
    ) {
        viewModelScope.launch {
            callApi(
                {
                    repository.getSearchResult(
                        word = id,
                        type = type,
                        startTime = startTime,
                        endTime = endTime
                    )
                },
                { state ->
                    when (state) {
                        is ApiResponseState.Succeeded<*> -> {
                            getSearchResult(state.data as SearchResultBean)
                        }
                        else -> Unit
                    }
                }
            )
        }
    }

    /** 處理搜尋結果 */
    private fun setResult(result: SearchResultBean) {
        when (result.type) {
            SearchResultTypeEnum.TOURNAMENT,
            SearchResultTypeEnum.TEAM,
            SearchResultTypeEnum.PLAYER -> {
                _directMatchType = when(result.directData) {
                    is SearchResultTournamentBean -> SearchTypeEnum.TOURNAMENT_ID
                    is SearchResultTeamBean -> SearchTypeEnum.TEAM_ID
                    is SearchResultPlayerBean -> SearchTypeEnum.PLAYER_ID
                    else -> null
                }
                _directMatchId = result.directData?.id
                _directData.value = result.directData
                result.matches
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { _combineResult.value = groupMatchesByDailyCount(it) }
                    ?: setState(DataState.DataEmpty)
            }
            else -> Unit
        }
    }

    /** 處理精準搜尋結果 */
    private fun groupMatchesByDailyCount(matches: List<SearchMatchBean>): List<SearchResultRaceItemType> {
        return matches.groupBy { match ->
            match.basicInfo.startTime.getFormatDate()
        }.toSortedMap().flatMap { (day, matchList) ->
            listOf(SearchResultRaceItemType.Header(day)) + matchList.map { SearchResultRaceItemType.Item(it) }
        }.let {
            if (it.isNotEmpty()) it + SearchResultRaceItemType.NoMore
            else it
        }
    }

    /** 處理賽事日期 */
    private fun setRaceDate(result: SearchResultBean) {
        if(racedDateMap.isNotEmpty()) return

        when (result.type) {
            SearchResultTypeEnum.TOURNAMENT,
            SearchResultTypeEnum.TEAM,
            SearchResultTypeEnum.PLAYER -> {
                _raceDateMap = result.matches
                    ?.asSequence()
                    ?.mapNotNull { match ->
                        match.basicInfo.startTime
                            .getFormatDate()
                            .split("/")
                            .takeIf { it.size == 3 }
                            ?.let { (year, month, day) ->
                                val cal = Calendar().apply {
                                    this.year = year.toInt()
                                    this.month = month.toInt()
                                    this.day = day.toInt()
                                }

                                cal.toString() to cal
                            }
                    }
                    ?.toMap()
                    ?.toMutableMap() ?: mutableMapOf()
            }
            else -> Unit
        }
    }

    /** 設定篩選時間 */
    fun setFilterTime(startTime: Long?, endTime: Long?) {
        _startTime = startTime
        _endTime = endTime
    }

    /** 新增收藏賽事 */
    fun addCollect(matchId: Long, handle: ((ApiResponseState) -> Unit)) {
        return callApi({ repository.addCollect(matchId) }, handle, false)
    }

    /** 移除收藏賽事 */
    fun removeCollect(matchId: Long, handle: ((ApiResponseState) -> Unit)) {
        return callApi({ repository.removeCollect(matchId) }, handle, false)
    }

    /** 設定選擇的日期 */
    fun setSelectedDate(date: Date?) {
        _selectedDateFlow.value = date
    }

    /** 取得選擇的日期 */
    fun getSelectedDate(): Date? = selectedDateFlow.value

    /** 設定當前頁面標題 */
    fun setCurrentTitle(title: String?) {
        _currentTitle = title
    }

    // ================== 遊戲供應商/分類直配相關 ==================

    /** 頁面模式 */
    private val _pageMode = MutableStateFlow<DirectPageMode?>(null)
    val pageMode: StateFlow<DirectPageMode?> = _pageMode.asStateFlow()

    /** 供應商資訊 */
    private val _supplierData = MutableStateFlow<GameSupplierDataModel?>(null)
    val supplierData: StateFlow<GameSupplierDataModel?> = _supplierData.asStateFlow()

    /** 供應商列表（與 GameContentFragment 一致，用於 Tab 轉換） */
    private val _gameSupplierList = MutableStateFlow<List<GameSupplierDataModel>>(emptyList())
    val gameSupplierList: StateFlow<List<GameSupplierDataModel>> = _gameSupplierList.asStateFlow()

    /** 供應商 / 遊戲類型 Tab（與 GameContentFragment 一致） */
    private val _supplierTabs = MutableStateFlow<List<SimpleTabDataModel>>(emptyList())
    val supplierTabs: StateFlow<List<SimpleTabDataModel>> = _supplierTabs.asStateFlow()

    /** 遊戲卡片列表（目前使用 mock 資料），結構與 hall 模組 GameContentData 對齊 */
    private val _vendorGames = MutableStateFlow<List<SearchGameContentData>>(emptyList())
    val vendorGames: StateFlow<List<SearchGameContentData>> = _vendorGames.asStateFlow()

    /** 當前選擇的排序類型（熱門 / 最新 / 火熱 / 冷門） */
    private val _selectedSortType = MutableStateFlow(GameSortType.HOT)
    val selectedSortType: StateFlow<GameSortType> = _selectedSortType.asStateFlow()

    /**
     * 載入供應商資訊（從 Room 查詢）
     */
    fun loadSupplierInfo(supplierId: Int) {
        viewModelScope.launch {
            _pageMode.value = DirectPageMode.VENDOR
            repository.querySupplierById(supplierId)?.let { supplier ->
                _supplierData.value = supplier
                loadSupplierTabs(gameTypeId = supplier.gameTypeId)
                generateMockGames(sortType = GameSortType.HOT, supplierId = supplierId)
            } ?: run {
                _vendorGames.value = emptyList()
                setState(DataState.DataEmpty)
            }
        }
    }

    /**
     * 從本地 DB 撈出一組供應商，更新供應商列表（用於 Tab 轉換）。
     * 與 GameContentFragment 使用相同的數據源。
     */
    suspend fun loadSupplierTabs(gameTypeId: Int = 4) {
        val suppliers = repository.querySuppliersByGameType(gameTypeId)
        _gameSupplierList.value = suppliers
    }

    /**
     * 產生一組 mock 的 GameContentData，用於先完成 UI 整合。
     * 參考 GameContentFragment 的數據結構，生成類似的 mock 數據。
     */
    fun generateMockGames(sortType: GameSortType, supplierId: Int?) {
        _selectedSortType.value = sortType

        // Mock 遊戲名稱列表（參考真實遊戲名稱）
        val mockGameNames = listOf(
            "Gates of Olympus 1000",
            "Sweet Bonanza",
            "Big Bass Bonanza",
            "Starlight Princess",
            "Sugar Rush",
            "Wild West Gold",
            "The Dog House",
            "Fire Strike",
            "Lucky Grace",
            "Book of Dead",
            "Razor Shark",
            "Reactoonz"
        )

        val mockList = (1..12).map { index ->
            val gameIndex = (index - 1) % mockGameNames.size
            val baseId = (supplierId ?: 0).toLong() * 1000 + index

            val name = mockGameNames[gameIndex] +
                    if (index > mockGameNames.size) " ${index - mockGameNames.size}" else ""

            val avatar = Avatar(
                url = "https://via.placeholder.com/300x400?text=${mockGameNames[gameIndex].replace(" ", "+")}",
                thumbhash = "",
                css = ""
            )

            // 使用 Int 範圍產生隨機值，再轉成 1 位小數的 Double，避免 FloatingPointRange 的 random() 解析問題
            val reward = when (sortType) {
                // 97.0% ~ 99.9%
                HOT_REWARD -> (970..999).random() / 10.0
                // 90.0% ~ 95.0%
                COLD_REWARD -> (900..950).random() / 10.0
                // 94.0% ~ 98.0%
                else -> (940..980).random() / 10.0
            }

            val hotOrCold = when (sortType) {
                HOT_REWARD -> HotColdType.HOT
                COLD_REWARD -> HotColdType.COLD
                else -> HotColdType.NONE
            }

            SearchGameContentData(
                id = baseId,
                name = name,
                avatar = avatar,
                online = (100..5000).random(),
                reward = reward,
                hasMore = index % 3 == 0,
                hotOrCold = hotOrCold
            )
        }

        _vendorGames.value = mockList
        setState(if (mockList.isEmpty()) DataState.DataEmpty else DataState.LoadSuccess)
    }

    /**
     * Tab / 排序切換時更新排序類型並重新產生 mock 列表。
     */
    fun switchSortType(sortType: GameSortType, supplierId: Int?) {
        generateMockGames(sortType, supplierId)
    }

    /**
     * 遊戲分類（例如「電子」「老虎機」）直配入口：
     * 根據 gameTypeId 載入對應的供應商 Tab，並產生一組 mock 遊戲列表。
     */
    fun loadGameCategory(gameTypeId: Int) {
        viewModelScope.launch {
            _pageMode.value = DirectPageMode.VENDOR
            loadSupplierTabs(gameTypeId)
            _supplierData.value = null // 分類模式沒有特定供應商
            generateMockGames(GameSortType.HOT, supplierId = null)
        }
    }
}