package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.TournamentListState
import arch.cayenne.module.home.data.constants.TournamentListUiState
import arch.cayenne.module.home.data.repo.TournamentListRepository
import arch.cayenne.module.home.ui.fragment.TournamentListType
import com.ibm.icu.text.Transliterator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class TournamentListViewModel : BaseViewModel() {
    private var type: TournamentListType = TournamentListType.MORE
    private var sportId = -1
    private val repo: TournamentListRepository by inject()
    private val transliterator: Transliterator by inject()

    private val _uiState = MutableLiveData<TournamentListUiState>()
    val uiState: LiveData<TournamentListUiState> get() = _uiState

    private var lastGroupedList: List<TournamentListItem> = emptyList()
    private var _tournamentList: List<BaseTournamentData> = emptyList()
    private var _searchQuery: String = ""
    private var _isSearchMode: Boolean = false
    val isSearchMode: Boolean get() = _isSearchMode

    private val _activeHeaderIndex = MutableLiveData<Int?>()
    val activeHeaderIndex: LiveData<Int?> get() = _activeHeaderIndex


    fun setSearchMode(enabled: Boolean) {
        _isSearchMode = enabled
        if (!enabled) {
            _searchQuery = ""
        }
        updateUiModel()
    }

    fun searchTournament(query: String) {
        _searchQuery = query
        updateUiModel()
    }

    fun setSportId(sportId: Int) {
        this.sportId = sportId
    }

    fun setType(type: TournamentListType) {
        this.type = type
    }

    fun getType() = type

    fun getTournaments() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getAllTournaments(type, sportId)
            val groupedList = processTournamentList(list)
            withContext(Dispatchers.Main) {
                _tournamentList = list
                lastGroupedList = groupedList
                updateUiModel(true)
            }
        }
    }

    private fun updateUiModel(isInit: Boolean = false) {
        val query = _searchQuery.trim()
        val hasData = lastGroupedList.isNotEmpty()
        val searchResult = if (_isSearchMode && query.isNotBlank()) {
            _tournamentList.filter { it.name.contains(query, ignoreCase = true) }
                .mapNotNull { tournament ->
                    val start = tournament.name.indexOf(query, ignoreCase = true)
                    if (start >= 0) {
                        val end = start + query.length
                        TournamentListItem.TournamentItem(tournament, start, end)
                    } else null
                }
        } else emptyList<TournamentListItem>()

        val state = when {
            // 非搜尋模式的狀態
            !_isSearchMode -> when {
                hasData -> if (isInit) TournamentListState.INIT_LIST else TournamentListState.RESTORE_LIST
                else -> TournamentListState.LIST_DATA_EMPTY
            }

            // 搜尋模式的狀態
            query.isBlank() -> TournamentListState.SEARCH_INIT
            searchResult.isNotEmpty() -> TournamentListState.SEARCH_MATCH
            else -> TournamentListState.SEARCH_DATA_EMPTY
        }

        val displayList = when (state) {
            TournamentListState.INIT_LIST, TournamentListState.RESTORE_LIST -> lastGroupedList
            TournamentListState.LIST_DATA_EMPTY -> emptyList()
            TournamentListState.SEARCH_INIT -> emptyList()
            TournamentListState.SEARCH_MATCH -> searchResult
            TournamentListState.SEARCH_DATA_EMPTY -> emptyList()
        }
        setState(state, displayList)
    }

    private fun setState(state: TournamentListState, displayList: List<TournamentListItem>) {
        _uiState.value = TournamentListUiState(state, displayList)
    }

    private suspend fun processTournamentList(tournaments: List<BaseTournamentData>): List<TournamentListItem> {
        return withContext(Dispatchers.IO) {
            val groupedMap = mutableMapOf<Char, MutableList<BaseTournamentData>>()
            val hotList = mutableListOf<BaseTournamentData>()
            val otherList = mutableListOf<BaseTournamentData>()
            val displayList = mutableListOf<TournamentListItem>()

            tournaments.forEach { tournament ->
                val pinyin = transliterator.transliterate(tournament.name).trim()
                val firstChar = pinyin.firstOrNull()?.uppercaseChar()
                when {
                    tournament.hot -> hotList.add(tournament)
                    firstChar != null && firstChar in 'A'..'Z' -> {
                        groupedMap.getOrPut(firstChar) { mutableListOf() }.add(tournament)
                    }

                    else -> otherList.add(tournament)
                }
            }

            // 添加熱門聯賽
            if (hotList.isNotEmpty()) {
                displayList.add(TournamentListItem.Header('*'))
                displayList.addAll(hotList.map {
                    TournamentListItem.TournamentItem(
                        it,
                        null,
                        null
                    )
                })
            }

            // 添加按字母排序的聯賽
            groupedMap.toSortedMap().forEach { (letter, list) ->
                displayList.add(TournamentListItem.Header(letter))
                displayList.addAll(list.map { TournamentListItem.TournamentItem(it, null, null) })
            }

            // 添加其他聯賽
            if (otherList.isNotEmpty()) {
                displayList.add(TournamentListItem.Header('#'))
                displayList.addAll(otherList.map {
                    TournamentListItem.TournamentItem(
                        it,
                        null,
                        null
                    )
                })
            }

            displayList
        }
    }

    fun getAvailableIndexLetters(): List<Char> {
        // 直接從 lastGroupedList 取出所有 Header 字母
        return lastGroupedList.filterIsInstance<TournamentListItem.Header>()
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
        // 回傳 lastGroupedList 中對應 header 的 index
        return lastGroupedList.indexOfFirst {
            it is TournamentListItem.Header && it.letter == letter
        }.takeIf { it >= 0 }
    }

    fun selectLetter(letter: Char) {
        val index = getHeaderIndex(letter)
        setActiveHeaderIndex(index)
    }
}