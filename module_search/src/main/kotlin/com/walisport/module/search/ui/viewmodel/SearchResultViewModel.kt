package com.walisport.module.search.ui.viewmodel

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchResultListItemType
import com.walisport.module.search.data.constants.SearchResultTypeEnum
import com.walisport.module.search.data.constants.SearchResultUiState
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.data.model.SearchResultBaseBean
import com.walisport.module.search.data.model.SearchResultBean
import com.walisport.module.search.data.model.SearchResultPlayerBeanBean
import com.walisport.module.search.data.model.SearchResultTeamBeanBean
import com.walisport.module.search.data.model.SearchResultTournamentBeanBean
import com.walisport.module.search.data.repo.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchResultViewModel : BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    private val _uiState = MutableStateFlow<SearchResultUiState>(SearchResultUiState.Loading)
    val uiState: StateFlow<SearchResultUiState> = _uiState

    private val _groupData = MutableStateFlow<List<SearchResultListItemType>>(emptyList())
    val groupData: StateFlow<List<SearchResultListItemType>> = _groupData.asStateFlow()

    fun setResult(context: Context, result: SearchResultBean) {
        when (result.type) {
            SearchResultTypeEnum.NONE -> {
                _uiState.value = SearchResultUiState.Empty
            }

            SearchResultTypeEnum.LIST -> {
                _uiState.value = SearchResultUiState.ResultList
                _groupData.value = groupSearchResults(context, result.dataList ?: emptyList())
            }

            SearchResultTypeEnum.TOURNAMENT,
            SearchResultTypeEnum.TEAM,
            SearchResultTypeEnum.PLAYER -> {
                _uiState.value = SearchResultUiState.DirectMatch(
                    type = result.type,
                )
            }
        }
    }

    private fun groupSearchResults(context: Context, list: List<SearchResultBaseBean>): List<SearchResultListItemType> {
        val groupedMap = mutableMapOf<String, List<SearchResultBaseBean>>()

        list.filterIsInstance<SearchResultTournamentBeanBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[ContextCompat.getString(context, R.string.tab_tournament)] = it
        }

        list.filterIsInstance<SearchResultTeamBeanBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[ContextCompat.getString(context, R.string.tab_team)] = it
        }

        list.filterIsInstance<SearchResultPlayerBeanBean>().takeIf { it.isNotEmpty() }?.let {
            groupedMap[ContextCompat.getString(context, R.string.tab_player)] = it
        }

        return groupedMap.flatMap { (title, items) ->
            listOf(SearchResultListItemType.Header(title)) +
                    items.map { SearchResultListItemType.Item(it) }
        }
    }

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
                                is SearchResultTournamentBeanBean -> SearchResultTypeEnum.TOURNAMENT
                                is SearchResultTeamBeanBean -> SearchResultTypeEnum.TEAM
                                is SearchResultPlayerBeanBean -> SearchResultTypeEnum.PLAYER
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

    fun getSearchResult(context: Context, data: SearchResultBaseBean, startTime: Long? = null, endTime: Long? = null) {
        viewModelScope.launch {
            setResult(
                context,
                repository.getSearchResult(
                    word = when (data) {
                        is SearchResultTournamentBeanBean,
                        is SearchResultTeamBeanBean,
                        is SearchResultPlayerBeanBean -> data.id.toString()
                        else -> ""
                    },
                    type = when (data) {
                        is SearchResultTournamentBeanBean -> SearchTypeEnum.TOURNAMENT_ID
                        is SearchResultTeamBeanBean -> SearchTypeEnum.TEAM_ID
                        is SearchResultPlayerBeanBean -> SearchTypeEnum.PLAYER_ID
                        else -> SearchTypeEnum.NORMAL_WORD
                    },
                    startTime = startTime,
                    endTime = endTime
                )
            )
        }
    }
}