package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.database.entity.SportDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.bet.repo.BetRepository
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.SportType
import arch.cayenne.module.home.data.repo.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class HomeViewModel : BaseViewModel() {
    companion object {
        const val TOURNAMENT_ALL_ID = 0
    }
    private val repository : HomeRepository by inject()
    private val balanceRepository: BalanceRepository by inject()
    var gameListPageIndex = 0
    private val betRepository: BetRepository by inject()
    private var currentPlayType : PlayType = PlayType.TODAY
    private var currentSportId: Int = 0
    val currentBalanceChange by lazy { MutableLiveData<Long>() }

    val sportsStatistical by lazy { MutableLiveData<List<SportDataModel>>() }

    val tournaments by lazy { MutableLiveData<List<TournamentDataModel>>() }

    val tenTournaments by lazy { MutableLiveData<List<TournamentDataModel>>() } // 今日/早盤
    val allTournaments by lazy { MutableLiveData<List<TournamentDataModel>>() } // 更多


    private val _selectedDate = MutableLiveData<Long>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<Long> = _selectedDate

    private val _selectedTournamentId = MutableLiveData<Int>()
    val selectedTournamentId: MutableLiveData<Int> get() = _selectedTournamentId

    private val _collapseTournamentDropdown = MutableLiveData<Boolean>()
    val collapseTournamentDropdown: MutableLiveData<Boolean> = _collapseTournamentDropdown

    fun requestCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = true
    }

    fun consumeCollapseTournamentDropdown() {
        _collapseTournamentDropdown.value = false
    }
    fun selectTournament(id: Int) {
        val currentList = tenTournaments.value.orEmpty()
        val existsInCurrent = currentList.any { it.id == id }

        if (existsInCurrent) {
            _selectedTournamentId.postValue(id)
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val playType = currentPlayType
                val tournament = repository.getTournamentById(id)
                if (tournament != null) {
                    val updatedList = currentList.toMutableList()
                    updatedList.add(tournament) // 👉 加到尾端
                    val fullList = ArrayList<TournamentDataModel>().apply {
                        add(TournamentDataModel.createAllItem(currentSportId))
                        addAll(updatedList)
                    }
                    "selectTournament list: $fullList".logd()
                    withContext(Dispatchers.Main) {
                        tenTournaments.value = fullList
                        _selectedTournamentId.value = id
                    }
                } else {
                    "Tournament ID:$id not found".loge(this::class.java.simpleName)
                }
            }
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        //觀察餘額變化
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeBalance().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }

    }

    //切換當前的一級選項(今日、早盤、冠軍)
    fun setCurrentPlayType(playType: PlayType) {
        currentPlayType = playType
        if (playType != PlayType.CHAMPION) {
            getCurrentSportStatistical()
        } else {
            getChampionTournament()
        }
    }

    private fun getChampionTournament() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getChampionTournament(currentSportId)
            withContext(Dispatchers.Main) {
                allTournaments.value = list
            }
        }
    }

    fun resetLiveData() {
        sportsStatistical.value = arrayListOf()
        tournaments.value = arrayListOf()
        tenTournaments.value = arrayListOf()
        allTournaments.value = arrayListOf()
    }

    fun getCurrentPlayType() = currentPlayType
    private fun getCurrentSportStatistical() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getSportStatistical()?.filter {
                SportType.fromId(it.id) != null  //去除目前沒有在code預設內的運動
            }
            if (list.isNullOrEmpty()) {
                //TODO 拿取sport錯誤
                "Get Sport List failed!!".loge(this@HomeViewModel::class.java.simpleName)
            } else {
                withContext(Dispatchers.Main) {
                    sportsStatistical.value = list
                }
            }
        }

    }

    //切換當前的二級選項(各項運動)
    fun setCurrentSport(sportId: Int) {
        currentSportId = sportId
        getCurrentTournament(sportId, false)
    }

    fun setShowAllTournaments(isShow: Boolean) {
        //TODO 做一個getAllTournament的方法
        if (isShow) getCurrentTournament(currentSportId, isShow)
    }

    fun getCurrentSportId() = currentSportId

    private fun getCurrentTournament(sportId: Int, isShowAll: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val playType = currentPlayType
            val list = if (isShowAll) {
                repository.getTournaments(playType.id, sportId)
            } else {
                repository.getTenTournaments(playType.id, sportId)
            }

            if (list.isNullOrEmpty()) {
                "Get Tournament List failed!!".loge(this::class.java.simpleName)
                return@launch
            }

            val fullList = ArrayList<TournamentDataModel>().apply {
                add(TournamentDataModel.createAllItem(sportId))
                addAll(list)
            }

            withContext(Dispatchers.Main) {
                if (isShowAll) {
                    allTournaments.value = fullList.filterNot { it.id == 0 }
                } else {
                    tenTournaments.value = fullList
                }
            }
        }
    }

    fun setSelectedDate(date: Long) {
        if (_selectedDate.value == date) return
        _selectedDate.value = date
    }


}