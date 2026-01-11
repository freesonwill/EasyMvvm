package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.TournamentListType
import arch.cayenne.module.home.data.repo.TournamentListRepository
import com.github.promeg.pinyinhelper.Pinyin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class TournamentListViewModel : BaseViewModel() {
    private var type: TournamentListType = TournamentListType.MORE
    private var sportId = -1
    private var playTypeId = 2
    private val repo: TournamentListRepository by inject()

    private var lastGroupedList: List<TournamentListItem> = emptyList()
    private var _tournamentList: List<BaseTournamentData> = emptyList()
    private var _searchQuery: String = ""
    private var _isSearchMode: Boolean = false
    val isSearchMode: Boolean get() = _isSearchMode

    private val _activeHeaderIndex = MutableLiveData<Int?>()
    val activeHeaderIndex: LiveData<Int?> get() = _activeHeaderIndex

    private val _tournamentsChange = MutableLiveData<List<TournamentListItem>>()
    val tournamentsChange: LiveData<List<TournamentListItem>> = _tournamentsChange

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.observeUserLogin()
                .filter { it == true && apiStateListener.value == DataState.NetworkUnavailable }
                .collect {
                    launch(Dispatchers.Main) {
                        getTournaments()
                    }
                }
        }
    }

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

    fun setPlayTypeId(playTypeId: Int) {
        this.playTypeId = playTypeId
    }

    fun setType(type: TournamentListType) {
        this.type = type
    }

    fun getType() = type

    fun getTournaments() {
        if (type == TournamentListType.MORE) {
            getMoreTournaments()
        } else {
            getChampionTournaments()
        }
    }

    private fun getMoreTournaments() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.queryTournaments(playTypeId, sportId)
            processAndUpdateTournamentList(list)
        }
    }

    private fun getChampionTournaments() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.queryChampionTournaments(sportId).apply {
                if (isNotEmpty()) {
                    processAndUpdateTournamentList(this)
                } else {
                    launch(Dispatchers.Main) {
                        callApi({
                            repo.getChampionTournament(sportId)
                        },{
                            if (it is ApiResponseState.Succeeded<*>) {
                                val list = it.dataAs<List<ChampionTournamentDataModel>>() ?: return@callApi
                                viewModelScope.launch {
                                    processAndUpdateTournamentList(list)
                                }
                            }
                        })
                    }
                }
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
                hasData -> if (isInit) HomeState.TournamentListState.InitList else HomeState.TournamentListState.RestoreList
                else -> HomeState.TournamentListState.ListDataEmpty
            }

            // 搜尋模式的狀態
            query.isBlank() -> HomeState.TournamentListState.SearcgInit
            searchResult.isNotEmpty() -> HomeState.TournamentListState.SearchMatch
            else -> HomeState.TournamentListState.SearchDataEmpty
        }

        val displayList = when (state) {
            HomeState.TournamentListState.InitList, HomeState.TournamentListState.RestoreList -> lastGroupedList
            HomeState.TournamentListState.ListDataEmpty -> emptyList()
            HomeState.TournamentListState.SearcgInit -> lastGroupedList
            HomeState.TournamentListState.SearchMatch -> searchResult
            HomeState.TournamentListState.SearchDataEmpty -> emptyList()
        }
        _tournamentsChange.value = displayList
        setState(state)
    }

    private suspend fun processAndUpdateTournamentList(tournaments: List<BaseTournamentData>) {
        withContext(Dispatchers.IO) {
            val groupedMap = mutableMapOf<Char, MutableList<BaseTournamentData>>()
            val hotList = mutableListOf<BaseTournamentData>()
            val otherList = mutableListOf<BaseTournamentData>()
            val displayList = mutableListOf<TournamentListItem>()

            tournaments.forEach { tournament ->
                val firstChar = tournament.name.firstOrNull()?.let { Pinyin.toPinyin(it).firstOrNull()?.uppercaseChar() }
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

            _tournamentList = tournaments
            lastGroupedList = displayList
            withContext(Dispatchers.Main) {
                updateUiModel(true)
            }
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