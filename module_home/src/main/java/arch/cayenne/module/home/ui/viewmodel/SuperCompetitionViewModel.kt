package arch.cayenne.module.home.ui.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.lib.database.entity.ChampionTournamentDataModel
import arch.cayenne.lib.database.entity.TournamentDataModel
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.playTypeToShowType
import arch.cayenne.module.home.utils.DateUtils
import arch.cayenne.module.home.utils.DateUtils.isSameDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import plugin.koin.KoinViewModel
import java.util.Locale

@KoinViewModel
class SuperCompetitionViewModel : SubHomeViewModel() {

    private val _dateList = MutableLiveData<List<EarlyDate>>()

    val dateList: MutableLiveData<List<EarlyDate>> = _dateList

    /**
     * 手动点击dateTab， 设置dateTab数据，切换联赛，切换运动时进行更新
     * 用于查询比赛数据
     */
    private val _selectedDate = MutableLiveData<Event<Long>>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<Event<Long>> = _selectedDate

    /**
     * 在日期栏上展示的时间, 注意：selectedDate和displayDate不一定相等， 日期栏的展示不能用selectedDate
     */
    private val _displayDate = MutableLiveData<EarlyDate>()

    val displayDate: MutableLiveData<EarlyDate> = _displayDate

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
        //更新displayDate
        _displayDate.value = dateList.value?.first { isSameDay(it.timestamp, date) }
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
            setCurrentTournamentIdList(listOf(tournament.id))
            currentList.forEach { tournamentCombo ->
                tournamentCombo.isSelected = false
            }
        } else if (tournament is ChampionTournamentDataModel) {
            _navigateToChampion.value = Event(tournament)
        }
    }


    fun setDisplayDate(date: EarlyDate) {
        _displayDate.value = date
    }

    fun getDisplayDate(timeStamp: Long): EarlyDate? {
        var earlyDate = dateList.value?.firstOrNull { isSameDay( it.timestamp , timeStamp) }

        if (earlyDate == null) {
            dateList.value?.let {
                if (it.isNotEmpty() && timeStamp > it.last().timestamp) {
                    earlyDate = it.last()
                }
            }
        }

        return earlyDate
    }

    /*
    *根据传入的时间戳， 转换成dateTab的index
     */
    fun getDisplayDateIndex(timestamp: Long): Int? {
        var index = dateList.value?.indexOfFirst { isSameDay( it.timestamp , timestamp) }

        if (index == -1) {
            dateList.value?.let {
                if (it.isNotEmpty() && timestamp > it.last().timestamp) {
                    index = it.size - 1
                }
            }
        }

        return index
    }

}

data class Date(
    val dateStr: String,
    val weekdayStr: String,
    val timestamp: Long,
    val type: DateType
)

enum class DateType {
    Date, Other
}