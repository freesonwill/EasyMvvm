package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.module.home.data.TournamentListItem
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

    private val _isLoading = MutableLiveData<Boolean>()

    private val _tournamentList = MutableLiveData<List<BaseTournamentData>>()

    private val _activeHeaderIndex = MutableLiveData<Int?>()
    val activeHeaderIndex: MutableLiveData<Int?> get() = _activeHeaderIndex

    private val letterPositionMap = mutableMapOf<Char, Int>()

    private var _lastSelectedLetter: Char? = null

    //輸入查詢字串
    private val _searchQuery = MutableLiveData<String?>()
    val searchQuery: String?
        get() = _searchQuery.value

    private val _isSearchMode = MutableLiveData(false)
    val isSearchMode: Boolean
        get() = _isSearchMode.value == true

    //統一觀察來源，搜尋結果或完整列表
    private val _displayList = MediatorLiveData<List<TournamentListItem>>()
    val displayList: MediatorLiveData<List<TournamentListItem>> get() = _displayList

    private var lastGroupedList: List<TournamentListItem> = emptyList()

    init {
        // 監聽原始數據變化，在 IO 線程處理
        displayList.addSource(_tournamentList) { tournaments ->
            if (!isSearchMode && tournaments.isNotEmpty()) {
                processTournamentListAsync(tournaments)
            }
        }

        // 監聽搜尋查詢變化
        displayList.addSource(_searchQuery) { updateDisplayList(it) }
    }

    fun setSearchMode(enabled: Boolean) {
        _isSearchMode.value = enabled
        if (!enabled) {
            // 只清空搜尋字串，觸發 updateDisplayList
            _searchQuery.value = null
        }
    }

    fun searchTournament(query: String) {
        _searchQuery.value = query
    }

    /**
     * 異步處理聯賽列表
     */
    private fun processTournamentListAsync(tournaments: List<BaseTournamentData>) {
        viewModelScope.launch(Dispatchers.IO) {
            val processedList = processTournamentList(tournaments)
            withContext(Dispatchers.Main) {
                _displayList.value = processedList
            }
        }
    }

    private fun updateDisplayList(searchString: String?) {
        val query = searchString?.trim().orEmpty()
        // 只在搜尋狀態下才會發送空列表或搜尋結果
        if (isSearchMode) {
            _displayList.value = if (query.isBlank()) {
                emptyList()
            } else {
                _tournamentList.value.orEmpty().filter {
                    it.name.contains(query, ignoreCase = true)
                }.mapNotNull { tournament ->
                    val start = tournament.name.indexOf(query, ignoreCase = true)
                    if (start >= 0) {
                        val end = start + query.length
                        TournamentListItem.TournamentItem(tournament, start, end)
                    } else null
                }
            }
        } else {
            // 非搜尋狀態下，直接顯示分組列表
            _displayList.value = lastGroupedList
        }
    }

    /**
     * 處理聯賽列表，按拼音首字母分組並創建顯示列表
     */
    private suspend fun processTournamentList(tournaments: List<BaseTournamentData>): List<TournamentListItem> {
        return withContext(Dispatchers.IO) {
            val groupedMap = mutableMapOf<Char, MutableList<BaseTournamentData>>()
            val hotList = mutableListOf<BaseTournamentData>()
            val otherList = mutableListOf<BaseTournamentData>()
            val displayList = mutableListOf<TournamentListItem>()

            letterPositionMap.clear()
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
                letterPositionMap['*'] = displayList.size - 1
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
                letterPositionMap[letter] = displayList.size
                displayList.add(TournamentListItem.Header(letter))
                displayList.addAll(list.map { TournamentListItem.TournamentItem(it, null, null) })
            }

            // 添加其他聯賽
            if (otherList.isNotEmpty()) {
                letterPositionMap['#'] = displayList.size
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

    fun setLastSelectedLetter(letter: Char?) {
        _lastSelectedLetter = letter
    }

    fun setActiveHeaderIndex(index: Int?) {
        if (_activeHeaderIndex.value != index) {
            _activeHeaderIndex.value = index
        }
    }

    fun getActiveHeaderIndex(): Int? = _activeHeaderIndex.value

    fun selectLetter(letter: Char) {
        val index = letterPositionMap[letter]
        if (index != null) {
            setActiveHeaderIndex(index)
        }
    }

    fun getHeaderIndex(letter: Char): Int? = letterPositionMap[letter]

    fun getAvailableIndexLetters(): List<Char> {
        return letterPositionMap.keys.sortedWith(compareBy {
            when (it) {
                '*' -> 0             // 熱門聯賽（星號）排最前
                in 'A'..'Z' -> it.code // 英文字母照 ASCII 排序 ('A' = 65, 'B' = 66 ...)
                '#' -> 999           // 其他無法分類的排字母之後
                else -> 1000         // 剩下非預期字元排最後
            }
        })
    }

    fun setType(type: TournamentListType) {
        this.type = type
    }

    fun getType() = type

    fun setSportId(sportId: Int) {
        this.sportId = sportId
    }

    fun getTournaments() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getAllTournaments(type, sportId)
            val groupedList = processTournamentList(list)
            withContext(Dispatchers.Main) {
                _tournamentList.value = list
                lastGroupedList = groupedList
                _displayList.value = groupedList
            }
        }
    }
}