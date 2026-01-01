package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.database.entity.BaseGameSupplierData
import com.walisport.module.search.data.model.SearchGameSupplierListItem
import com.walisport.module.search.data.repo.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import com.github.promeg.pinyinhelper.Pinyin

/**
 * Search 模組專用的供應商列表 ViewModel，用於 BottomSheet。
 * 與 hall 模組的 GameContentListViewModel 對應，但完全獨立實作。
 */
@KoinViewModel
class SearchGameContentListViewModel : BaseViewModel() {
    private var sportId = -1

    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    private var lastGroupedList: List<SearchGameSupplierListItem> = emptyList()
    private var _tournamentList: List<BaseGameSupplierData> = emptyList()
    private var _searchQuery: String = ""
    private var _isSearchMode: Boolean = false
    val isSearchMode: Boolean get() = _isSearchMode

    private val _activeHeaderIndex = MutableLiveData<Int?>()
    val activeHeaderIndex: LiveData<Int?> get() = _activeHeaderIndex

    private val _tournamentsChange = MutableLiveData<List<SearchGameSupplierListItem>>()
    val tournamentsChange: LiveData<List<SearchGameSupplierListItem>> = _tournamentsChange

    fun setSearchMode(enabled: Boolean) {
        _isSearchMode = enabled
        if (!enabled) {
            _searchQuery = ""
        }
        updateUiModel()
    }

    fun setSelectIds(ids: List<Int>) {
        viewModelScope.launch {
            repository.setSupplierSelectIds(sportId, ids)
        }
    }

    fun searchTournament(query: String) {
        _searchQuery = query
        updateUiModel()
    }

    fun setSportId(sportId: Int) {
        this.sportId = sportId
    }

    fun getTournaments(type: Int) {
        getMoreTournaments(type)
    }

    private fun getMoreTournaments(type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.querySuppliersByGameType(sportId)
            LogUtils.e("getMoreTournaments--------->${list}")
            processAndUpdateTournamentList(list)
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.observeSupplierByGameTypeId(type)
                .collect {
                    launch(Dispatchers.Main) {
                        processAndUpdateTournamentList(it)
                    }
                }
        }
    }

    private fun updateUiModel(isInit: Boolean = false) {
        val query = _searchQuery.trim()
        val searchResult = if (_isSearchMode && query.isNotBlank()) {
            _tournamentList.filter { it.name.contains(query, ignoreCase = true) }
                .mapNotNull { tournament ->
                    val start = tournament.name.indexOf(query, ignoreCase = true)
                    if (start >= 0) {
                        val end = start + query.length
                        SearchGameSupplierListItem.GameSupplierItem(tournament, start, end)
                    } else null
                }
        } else emptyList<SearchGameSupplierListItem>()
        
        if (_isSearchMode) {
            _tournamentsChange.value = searchResult
        } else {
            _tournamentsChange.value = lastGroupedList
        }
    }

    private suspend fun processAndUpdateTournamentList(tournaments: List<BaseGameSupplierData>) {
        withContext(Dispatchers.IO) {
            val groupedMap = mutableMapOf<Char, MutableList<BaseGameSupplierData>>()
            val hotList = mutableListOf<BaseGameSupplierData>()
            val otherList = mutableListOf<BaseGameSupplierData>()
            val displayList = mutableListOf<SearchGameSupplierListItem>()

            tournaments.forEach { tournament ->
                val firstChar = tournament.name.firstOrNull()
                    ?.let { Pinyin.toPinyin(it).firstOrNull()?.uppercaseChar() }
                when {
                    tournament.hot -> hotList.add(tournament)
                    firstChar != null && firstChar in 'A'..'Z' -> {
                        groupedMap.getOrPut(firstChar) { mutableListOf() }.add(tournament)
                    }
                    else -> otherList.add(tournament)
                }
            }

            // 添加熱門供應商
            if (hotList.isNotEmpty()) {
                displayList.add(SearchGameSupplierListItem.Header('*'))
                displayList.addAll(hotList.map {
                    SearchGameSupplierListItem.GameSupplierItem(
                        it,
                        null,
                        null
                    )
                })
            }

            // 添加按字母排序的供應商
            groupedMap.toSortedMap().forEach { (letter, list) ->
                displayList.add(SearchGameSupplierListItem.Header(letter))
                displayList.addAll(list.map {
                    SearchGameSupplierListItem.GameSupplierItem(
                        it,
                        null,
                        null
                    )
                })
            }

            // 添加其他供應商
            if (otherList.isNotEmpty()) {
                displayList.add(SearchGameSupplierListItem.Header('#'))
                displayList.addAll(otherList.map {
                    SearchGameSupplierListItem.GameSupplierItem(
                        it,
                        null,
                        null
                    )
                })
            }

            _tournamentList = tournaments
            lastGroupedList = displayList
            withContext(Dispatchers.Main) {
                updateUiModel(true)
            }
        }
    }

    fun getAvailableIndexLetters(): List<Char> {
        return lastGroupedList.filterIsInstance<SearchGameSupplierListItem.Header>()
            .map { it.letter }
            .sortedWith(compareBy {
                when (it) {
                    '*' -> 0
                    in 'A'..'Z' -> it.code
                    '#' -> 999
                    else -> 1000
                }
            })
    }

    fun setActiveHeaderIndex(index: Int?) {
        if (_activeHeaderIndex.value != index) {
            _activeHeaderIndex.value = index
        }
    }

    fun getActiveHeaderIndex(): Int? = _activeHeaderIndex.value

    fun getHeaderIndex(letter: Char): Int? {
        return lastGroupedList.indexOfFirst {
            it is SearchGameSupplierListItem.Header && it.letter == letter
        }.takeIf { it >= 0 }
    }

    fun selectLetter(letter: Char) {
        val index = getHeaderIndex(letter)
        setActiveHeaderIndex(index)
    }
}

