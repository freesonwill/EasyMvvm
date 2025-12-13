package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.database.entity.BaseGameSupplierData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.module.home.data.GameSupplierListItem
import arch.cayenne.module.home.data.repo.GameSupplierListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel
import com.github.promeg.pinyinhelper.Pinyin
import org.koin.core.parameter.parametersOf

@KoinViewModel
class GameContentListViewModel : BaseViewModel() {
    private var sportId = -1

    private val repo: GameSupplierListRepository by inject { parametersOf(viewModelScope) }

    private var lastGroupedList: List<GameSupplierListItem> = emptyList()
    private var _tournamentList: List<BaseGameSupplierData> = emptyList()
    private var _searchQuery: String = ""
    private var _isSearchMode: Boolean = false
    val isSearchMode: Boolean get() = _isSearchMode

    private val _activeHeaderIndex = MutableLiveData<Int?>()
    val activeHeaderIndex: LiveData<Int?> get() = _activeHeaderIndex

    private val _tournamentsChange = MutableLiveData<List<GameSupplierListItem>>()
    val tournamentsChange: LiveData<List<GameSupplierListItem>> = _tournamentsChange

    fun setSearchMode(enabled: Boolean) {
        _isSearchMode = enabled
        if (!enabled) {
            _searchQuery = ""
        }
        updateUiModel()
    }

    fun setSelectIds(ids : List<Int>){
        repo.setSupplierSelectIds(sportId,ids)
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
            val list = repo.queryGameSuppliers(sportId)
            LogUtils.e("getMoreTournaments--------->${list}")
            processAndUpdateTournamentList(list)
        }

        viewModelScope.launch(Dispatchers.IO) {
            repo.observeSupplierByGameTypeId(type)
                .collect {
                    launch(Dispatchers.Main) {
                        processAndUpdateTournamentList(it)
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
                        GameSupplierListItem.GameSupplierItem(tournament, start, end)
                    } else null
                }
        } else emptyList<GameSupplierListItem>()
        if (_isSearchMode){
            _tournamentsChange.value = searchResult
        }else{
            _tournamentsChange.value = lastGroupedList
        }

    }

    private suspend fun processAndUpdateTournamentList(tournaments: List<BaseGameSupplierData>) {
        withContext(Dispatchers.IO) {
            val groupedMap = mutableMapOf<Char, MutableList<BaseGameSupplierData>>()
            val hotList = mutableListOf<BaseGameSupplierData>()
            val otherList = mutableListOf<BaseGameSupplierData>()
            val displayList = mutableListOf<GameSupplierListItem>()

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

            // 添加熱門聯賽
            if (hotList.isNotEmpty()) {
                displayList.add(GameSupplierListItem.Header('*'))
                displayList.addAll(hotList.map {
                    GameSupplierListItem.GameSupplierItem(
                        it,
                        null,
                        null
                    )
                })
            }

            // 添加按字母排序的聯賽
            groupedMap.toSortedMap().forEach { (letter, list) ->
                displayList.add(GameSupplierListItem.Header(letter))
                displayList.addAll(list.map {
                    GameSupplierListItem.GameSupplierItem(
                        it,
                        null,
                        null
                    )
                })
            }

            // 添加其他聯賽
            if (otherList.isNotEmpty()) {
                displayList.add(GameSupplierListItem.Header('#'))
                displayList.addAll(otherList.map {
                    GameSupplierListItem.GameSupplierItem(
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
        return lastGroupedList.filterIsInstance<GameSupplierListItem.Header>()
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
            it is GameSupplierListItem.Header && it.letter == letter
        }.takeIf { it >= 0 }
    }

    fun selectLetter(letter: Char) {
        val index = getHeaderIndex(letter)
        setActiveHeaderIndex(index)
    }
}