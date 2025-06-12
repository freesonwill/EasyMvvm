package com.walisport.module.search.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchResultListItemType
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.data.model.SearchResultPlayerBean
import com.walisport.module.search.data.model.SearchResultTeamBean
import com.walisport.module.search.data.model.SearchResultTournamentBean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchResultPageViewModel: BaseViewModel() {
    /** 整理後搜尋結果 列表用 */
    private val _groupData = MutableStateFlow<List<SearchResultListItemType>>(emptyList())
    val groupData: StateFlow<List<SearchResultListItemType>> = _groupData.asStateFlow()

    /** 處理搜尋結果 */
    fun setResult(result: SearchResultBean) {
        when (result.type) {
            SearchResultTypeEnum.LIST -> {
                _groupData.value = groupSearchResults(result.dataList ?: emptyList())
            }
            else -> Unit
        }
    }

    /** 分類搜尋結果 列表用 */
    private fun groupSearchResults(list: List<SearchResultBaseBean>): List<SearchResultListItemType> {
        val groupedMap = mutableMapOf<Int, List<SearchResultBaseBean>>()

        list.filterIsInstance<SearchResultTournamentBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[R.string.tab_tournament] = it
        }

        list.filterIsInstance<SearchResultTeamBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[R.string.tab_team] = it
        }

        list.filterIsInstance<SearchResultPlayerBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[R.string.tab_player] = it
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
}