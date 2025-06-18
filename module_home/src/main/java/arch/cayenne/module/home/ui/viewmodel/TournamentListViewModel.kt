package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.repo.TournamentListRepository
import arch.cayenne.module.home.ui.fragment.TournamentListType
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

    private val _isLoading = MutableLiveData<Boolean>()

    val tournamentList by lazy { MutableLiveData<List<BaseTournamentData>>() }

    private val _activeHeaderIndex = MutableLiveData<Int?>()
    val activeHeaderIndex: MutableLiveData<Int?> get() = _activeHeaderIndex

    private val letterPositionMap = mutableMapOf<Char, Int>()

    private var _lastSelectedLetter: Char? = null

    //輸入查詢字串
    private val searchQuery = MutableLiveData<String?>()
    val isSearchMode: Boolean
        get() = !searchQuery.value.isNullOrBlank()

    //統一觀察來源，搜尋結果或完整列表
    val displayList = MediatorLiveData<List<TournamentListItem>>()

    init {
        displayList.addSource(searchQuery) { updateDisplayList() }
    }

    fun searchTournament(query: String) {
        searchQuery.value = query
    }

    fun clearSearch() {
        searchQuery.value = null
    }

    private fun updateDisplayList() {
        val query = searchQuery.value?.trim().orEmpty()
        if (query.isBlank()) {
            displayList.value =
                tournamentList.value?.map { TournamentListItem.TournamentItem(it, null, null) }
                    ?: emptyList()
            return
        }

        val filtered = tournamentList.value.orEmpty().filter {
            it.name.contains(query, ignoreCase = true)
        }.mapNotNull { tournament ->
            val start = tournament.name.indexOf(query, ignoreCase = true)
            if (start >= 0) {
                val end = start + query.length
                TournamentListItem.TournamentItem(
                    tournament,
                    start,
                    end
                )
            } else null
        }
        displayList.value = filtered
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

    fun setLetterPositionMap(map: Map<Char, Int>) {
        letterPositionMap.clear()
        letterPositionMap.putAll(map)
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

    fun getTournaments(onResult: (Boolean, List<BaseTournamentData>) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getAllTournaments(type, sportId)
            withContext(Dispatchers.Main) {
                tournamentList.value = list
                onResult(list.isNotEmpty(), list)
            }
        }
    }
}