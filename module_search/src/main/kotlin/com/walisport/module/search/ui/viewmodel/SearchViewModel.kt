package com.walisport.module.search.ui.viewmodel

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.getFormatDate
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.data.constants.SearchResultListItemType
import com.walisport.module.search.data.constants.SearchResultRaceItemType
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.constants.SearchResultUiState
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.data.model.SearchDailyMatchBean
import com.walisport.module.search.data.model.SearchMatchBean
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.data.model.SearchResultTeamBean
import com.walisport.module.search.data.model.SearchResultTournamentBean
import com.walisport.module.search.data.repo.SearchRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import java.text.SimpleDateFormat
import java.util.Date

@KoinViewModel
class SearchViewModel : BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    /** 搜尋關鍵字 */
    private val _searchKey = MutableSharedFlow<String>()
    val searchKey: SharedFlow<String> = _searchKey.asSharedFlow()

    /** 歷史搜尋紀錄 */
    private val _recordList = MutableLiveData<List<String>>()
    val searchRecord: LiveData<List<String>> = _recordList

    /** 新增一筆搜尋紀錄 */
    private val _addOneRecord = MutableSharedFlow<String>()
    val addOneRecord: SharedFlow<String> = _addOneRecord.asSharedFlow()

    /** 熱門搜尋關鍵字 */
    private val _searchHotWord = MutableLiveData<List<String>>()
    val searchHotWord: LiveData<List<String>> = _searchHotWord

    /** 推薦搜尋關鍵字 */
    private val _searchRecommend = MutableLiveData<List<String>>()
    val searchRecommend: LiveData<List<String>> = _searchRecommend

    /** 搜尋結果頁 UI 狀態 */
    private val _uiState = MutableSharedFlow<SearchResultUiState>()
    val uiState: SharedFlow<SearchResultUiState> = _uiState.asSharedFlow()

    /** 整理後搜尋結果 列表用 */
    private val _groupData = MutableStateFlow<List<SearchResultListItemType>>(emptyList())
    val groupData: StateFlow<List<SearchResultListItemType>> = _groupData.asStateFlow()

    /** 精準搜尋結果 */
    private val _directData = MutableStateFlow<SearchResultBaseBean?>(null)
    val directData: StateFlow<SearchResultBaseBean?> = _directData.asStateFlow()

    /** 整理後搜尋結果 賽事用 */
    private val _combineResult = MutableStateFlow<List<SearchResultRaceItemType>>(emptyList())
    val combineResult: StateFlow<List<SearchResultRaceItemType>> = _combineResult.asStateFlow()

    /** 漸層背景顏色 */
    private val _gradientBgColor = MutableSharedFlow<Int?>(replay = 1)
    val gradientBgColor: SharedFlow<Int?> = _gradientBgColor.asSharedFlow()

    /** 選擇的日期 */
    private val _selectedDateFlow = MutableStateFlow(Date())
    val selectedDateFlow: StateFlow<Date> = _selectedDateFlow.asStateFlow()

    /** 導航事件 */
    private val _navEvent = MutableSharedFlow<SearchNavigationEvent>()
    val navEvent: SharedFlow<SearchNavigationEvent> = _navEvent.asSharedFlow()


    /** 以 UID 取得搜尋紀錄 */
    fun getRecordByUID() {
        viewModelScope.launch {
            _recordList.value = repository.getRecordByUID()
        }
    }

    /** 移除所有搜尋紀錄 */
    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }

    /** 新增一筆搜尋紀錄 */
    fun addOneRecord(key: String) {
        viewModelScope.launch {
            _addOneRecord.emit(key)
            repository.addOneRecord(key)
        }
    }

    /** 刪除一筆搜尋紀錄 */
    fun deleteOneRecord(keyword: String?) {
        viewModelScope.launch {
            repository.deleteOneRecord(keyword)
        }
    }

    /** 取得推薦關鍵字結果 */
    fun getSearchRecommend(keyword: String? = "") {
        viewModelScope.launch {
            _searchRecommend.value = repository.getSearchRecommend(keyword)
        }
    }

    /** 清除推薦關鍵字結果 */
    fun clearSearchRecommend() {
        _searchRecommend.value = emptyList()
    }

    /** 取得熱門搜尋關鍵字 */
    fun getSearchHotWord() {
        viewModelScope.launch {
            _searchHotWord.value = repository.getSearchHotWord()
        }
    }

    /** 設定搜尋結果頁 UI 狀態 */
    private fun setUiState(state: SearchResultUiState) {
        viewModelScope.launch {
            _uiState.emit(state)
        }
    }

    /** 重置搜尋結果 */
    private fun resetResult() {
        _groupData.value = emptyList()
        _directData.value = null
        _combineResult.value = emptyList()
    }

    /** 取得搜尋結果 */
    fun getSearchResult(context: Context, keyword: String) {
        viewModelScope.launch {
            resetResult()
            setUiState(SearchResultUiState.Loading)
            setResult(context, repository.getSearchResult(keyword))
        }
    }

    /** 處理搜尋結果 */
    private fun setResult(context: Context, result: SearchResultBean) {
        when (result.type) {
            SearchResultTypeEnum.NONE -> {
                setUiState(SearchResultUiState.Empty)
            }

            SearchResultTypeEnum.LIST -> {
                setUiState(SearchResultUiState.ResultList)
                _groupData.value = groupSearchResults(context, result.dataList ?: emptyList())
            }

            SearchResultTypeEnum.TOURNAMENT,
            SearchResultTypeEnum.TEAM,
            SearchResultTypeEnum.PLAYER -> {
                setUiState(SearchResultUiState.DirectMatch(type = result.type))
                _directData.value = result.directData
                _combineResult.value = groupMatchesByDailyCount(
                    dailyCounts = result.dailyCount?.filter { it.count != 0 } ?: emptyList(),
                    matches = result.matches ?: emptyList()
                )
            }
        }
    }

    /** 分類搜尋結果 列表用 */
    private fun groupSearchResults(context: Context, list: List<SearchResultBaseBean>): List<SearchResultListItemType> {
        val groupedMap = mutableMapOf<String, List<SearchResultBaseBean>>()

        list.filterIsInstance<SearchResultTournamentBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[ContextCompat.getString(context, R.string.tab_tournament)] = it
        }

        list.filterIsInstance<SearchResultTeamBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[ContextCompat.getString(context, R.string.tab_team)] = it
        }

        list.filterIsInstance<SearchResultPlayerBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[ContextCompat.getString(context, R.string.tab_player)] = it
        }

        return groupedMap.flatMap { (title, items) ->
            listOf(SearchResultListItemType.Header(title)) +
                    items.map { SearchResultListItemType.Item(it) }
        }
    }

    /** 取得搜尋結果，包含分類標題 */
    fun getLimitGroupSearResults(
        list: List<SearchResultListItemType>,
        maxPerGroup: Int = 5
    ): List<SearchResultListItemType> {
        val result = mutableListOf<SearchResultListItemType>()
        var currentGroupCount = 0
        var totalGroupCount = 0

        for (item in list) {
            when (item) {
                is SearchResultListItemType.Header -> {
                    result += item
                    currentGroupCount = 0
                    totalGroupCount = 0
                }

                is SearchResultListItemType.Item -> {
                    if (currentGroupCount < maxPerGroup) {
                        result += item
                        currentGroupCount++
                    }
                    totalGroupCount++
                    if (totalGroupCount == maxPerGroup + 1) {
                        result += SearchResultListItemType.More(
                            type = when (item.data) {
                                is SearchResultTournamentBean -> SearchResultTypeEnum.TOURNAMENT
                                is SearchResultTeamBean -> SearchResultTypeEnum.TEAM
                                is SearchResultPlayerBean -> SearchResultTypeEnum.PLAYER
                                else -> SearchResultTypeEnum.NONE
                            }
                        )
                    }
                }

                else -> Unit
            }
        }

        return result
    }

    /** 取得精準搜尋結果 */
    fun getSearchResult(context: Context, data: SearchResultBaseBean, startTime: Long? = null, endTime: Long? = null) {
        viewModelScope.launch {
            resetResult()
            setResult(
                context,
                repository.getSearchResult(
                    word = when (data) {
                        is SearchResultTournamentBean,
                        is SearchResultTeamBean,
                        is SearchResultPlayerBean -> data.id.toString()
                        else -> ""
                    },
                    type = when (data) {
                        is SearchResultTournamentBean -> SearchTypeEnum.TOURNAMENT_ID
                        is SearchResultTeamBean -> SearchTypeEnum.TEAM_ID
                        is SearchResultPlayerBean -> SearchTypeEnum.PLAYER_ID
                        else -> SearchTypeEnum.NORMAL_WORD
                    },
                    startTime = startTime,
                    endTime = endTime
                )
            )
        }
    }

    /** 處理精準搜尋結果 */
    private fun groupMatchesByDailyCount(dailyCounts: List<SearchDailyMatchBean>, matches: List<SearchMatchBean>): List<SearchResultRaceItemType> {
        return matches.groupBy { match ->
            match.basicInfo.startTime.getFormatDate()
        }.toSortedMap().flatMap { (day, matchList) ->
            listOf(SearchResultRaceItemType.Header(day)) + matchList.map { SearchResultRaceItemType.Item(it) }
        }
    }

    /** 設定漸層背景顏色 */
    fun setGradientBgColor(color: Int? = null) {
        viewModelScope.launch {
            _gradientBgColor.emit(color)
        }
    }

    /** 設定選擇的日期 */
    fun setSelectedDate(date: Date) {
        _selectedDateFlow.value = date
    }

    /** 設定搜尋關鍵字 */
    fun setSearchKey(key: String) {
        viewModelScope.launch {
            _searchKey.emit(key)
        }
    }

    /** 導航到其他頁面 */
    fun navigateTo(event: SearchNavigationEvent) {
        viewModelScope.launch {
            _navEvent.emit(event)
        }
    }
}