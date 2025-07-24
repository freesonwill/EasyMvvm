package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.getFormatDate
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import java.util.Date

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

    /** 當前頁面標題 */
    private var _currentTitle: String? = null
    val currentTitle: String?
        get() = _currentTitle

    /** 暫存有比賽的日期 */
    private var _raceDateMap: MutableMap<String, Calendar> = mutableMapOf()
    val racedDateMap: Map<String, Calendar>
        get() = _raceDateMap

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
}