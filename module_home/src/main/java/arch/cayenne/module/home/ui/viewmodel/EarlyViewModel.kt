package arch.cayenne.module.home.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.playTypeToShowType
import arch.cayenne.module.home.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import plugin.koin.KoinViewModel
import java.util.Locale

@KoinViewModel
class EarlyViewModel : SubHomeViewModel() {

    private val _dateList = MutableLiveData<List<EarlyDate>>()

    val dateList: MutableLiveData<List<EarlyDate>> = _dateList

    private val _selectedDate = MutableLiveData<Event<Long>>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<Event<Long>> = _selectedDate

    init {
        val futureDays = DateUtils.getFutureDays(
            31,
            Locale.getDefault(),
            KoinJavaComponent.getKoin().get<Application>().getString(R.string.first_day_title),
            "M-dd",
            "E"
        ).take(8)

        //需要拿第8天的timeInMillis， 作为其他日期的timeInMillis
        val last = futureDays.last()

        //添加其他
        _dateList.value =
            futureDays.take(7).map { EarlyDate(it.first, it.second, it.third, EarlyDateType.Date) }
                .toMutableList().plus(
                    EarlyDate(
                        KoinJavaComponent.getKoin().get<Application>()
                            .getString(R.string.other_day_title),
                        "",
                        last.third, EarlyDateType.Other
                    )
                )
    }


    override fun initViewModel() {
        super.initViewModel()
    }

    private suspend fun setCurrentSelectedDate() {
        val date = repository.getCurrentSelectedDate(
            currentPlayTypeId.playTypeToShowType(),
            currentSportId
        ) ?: 0L
        withContext(Dispatchers.Main) {
            selectedDate(date)
        }
    }

    suspend fun selectedDate(date: Long) {
        if (_selectedDate.value?.peekContent() == date) return
        withContext(Dispatchers.IO) {
            repository.updateSelectedDate(
                currentPlayTypeId.playTypeToShowType(),
                currentSportId,
                date
            )
        }
        _selectedDate.value = Event(date)
    }

    //切換當前的二級選項(各項運動)
    override fun setCurrentSport(sportId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.Main) {
                _selectedDate.value =
                    Event(HomeViewModel.DEFAULT_DATE)  //先送出一個初始值，避免MatchListPage生成時會拿到舊值先拿取資料
            }
            _currentSportId.value = sportId
            repository.updateSelectedSportId(currentPlayTypeId, currentSportId)
            if (currentPlayTypeId != PlayType.CHAMPION.id) {
                "On setCurrentSport -> Clear Tournaments LiveData & Update Tournaments from API".logi(
                    this::class.java.simpleName
                )
                getCurrentTournament()
            }
        }
    }


    override fun onTournamentListSelected(tournament: BaseTournamentData) {
        // 標記外部tab已切換，下次打開彈窗時需要清空篩選
        hasTournamentTabSwitched = true

        if (tournament is TournamentDataModel) {
            val currentList = tournaments.value?.peekContent() ?: return
            setCurrentTournamentId(tournament.id)
            if (!currentList.any { it.id == tournament.id }) {
                viewModelScope.launch { selectedDate(0L) }
                tournaments.value = Event(currentList.map {
                    it.apply {
                        isSelected = false
                    }
                } + tournament.apply { isSelected = true })
            } else {
                tournaments.value =
                    Event(currentList.map { it.apply { isSelected = tournament.id == id } })
            }
        } else if (tournament is ChampionTournamentDataModel) {
            _navigateToChampion.value = Event(tournament)
        }
    }


}

data class EarlyDate(
    val dateStr: String,
    val weekdayStr: String,
    val timestamp: Long,
    val type: EarlyDateType
)

enum class EarlyDateType {
    Date, Other
}