package arch.cayenne.module.home.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.common.utils.ext.VIPDataExt
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.module.home.R
import arch.cayenne.module.home.TournamentCombo
import arch.cayenne.module.home.data.BiDirectionalDate
import arch.cayenne.module.home.data.BiDirectionalDateType
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.playTypeToShowType
import arch.cayenne.module.home.data.repo.HomeRepository
import arch.cayenne.module.home.utils.DateUtils
import arch.cayenne.module.home.utils.DateUtils.isSameDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent
import plugin.koin.KoinViewModel
import java.util.Locale

@KoinViewModel
class SuperCompetitionViewModel : BaseViewModel() {

    val repository: HomeRepository by inject()

    var currentPlayTypeId: Int = PlayType.EARLY.id
        private set

    val _currentSportId: MutableStateFlow<Int> = MutableStateFlow(SportEnum.Default.id)
    val currentSportId: Int
        get() = _currentSportId.value

    private val _currentSportIdChange = MutableLiveData<Event<Int>>()
    val currentSportIdChange: LiveData<Event<Int>> = _currentSportIdChange

    private val _dateList = MutableLiveData<List<BiDirectionalDate>>()

    val dateList: MutableLiveData<List<BiDirectionalDate>> = _dateList

    /**
     * 手动点击dateTab， 设置dateTab数据，切换联赛，切换运动时进行更新
     * 用于查询比赛数据
     */
    private val _selectedDate = MutableLiveData<Event<Long>>() // Pair<leagueId, date>
    val selectedDate: MutableLiveData<Event<Long>> = _selectedDate

    /**
     * 在日期栏上展示的时间, 注意：selectedDate和displayDate不一定相等， 日期栏的展示不能用selectedDate
     */
    private val _displayDate = MutableLiveData<BiDirectionalDate>()

    val displayDate: MutableLiveData<BiDirectionalDate> = _displayDate

    // VIP 等級數據
    private val _onVipListener = MutableLiveData<UserDataBean>()
    val onVipListener: LiveData<UserDataBean> get() = _onVipListener

    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val _selectedSkinType = MutableLiveData<Event<String>>()
    val selectedSkinType: LiveData<Event<String>> = _selectedSkinType

    val tournaments by lazy { MutableLiveData<Event<List<TournamentCombo>>>() } // 今日/早盤



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
            futureDays.take(7).map { BiDirectionalDate(it.first, it.second, it.third, BiDirectionalDateType.Date) }
                .toMutableList().plus(
                    BiDirectionalDate(
                        KoinJavaComponent.getKoin().get<Application>()
                            .getString(R.string.other_day_title),
                        "",
                        last.third, BiDirectionalDateType.Other
                    )
                )
    }


    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            skinManager.skinFlow.collect {
                _selectedSkinType.value = Event(it)
            }
        }

        viewModelScope.launch {
            repository.observeUserInfo().collect {
                _onVipListener.value = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _currentSportId.collect {
                launch(Dispatchers.Main) {
                    _currentSportIdChange.value = Event(it)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.observeLoginChange()
                .filter {
                    it && (!repository.isPreloadSuccess()
                            || apiStateListener.value == DataState.NetworkUnavailable
                            || apiStateListener.value == HomeState.Sport.LoadFailure
                            || apiStateListener.value == HomeState.Tournament.LoadFailure)
                }
                .collect {
                    launch(Dispatchers.Main) {

                    }
                }
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
        //更新displayDate
        _displayDate.value = dateList.value?.first { isSameDay(it.timestamp, date) }
    }

    //切換當前的二級選項(各項運動)
    fun setCurrentSport(sportId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            launch(Dispatchers.Main) {
                _selectedDate.value =
                    Event(HomeViewModel.DEFAULT_DATE)  //先送出一個初始值，避免MatchListPage生成時會拿到舊值先拿取資料
            }
            _currentSportId.value = sportId
        }
    }

    fun setDisplayDate(date: BiDirectionalDate) {
        _displayDate.value = date
    }

    fun getDisplayDate(timeStamp: Long): BiDirectionalDate? {
        var earlyDate = dateList.value?.firstOrNull { isSameDay(it.timestamp, timeStamp) }

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
        var index = dateList.value?.indexOfFirst { isSameDay(it.timestamp, timestamp) }

        if (index == -1) {
            dateList.value?.let {
                if (it.isNotEmpty() && timestamp > it.last().timestamp) {
                    index = it.size - 1
                }
            }
        }

        return index
    }

    fun getTournamentsName(tournamentId: List<Int>): String {

//        val stringList = mutableListOf<String>()
//        tournamentId.forEach { id ->
//            val tournamentCombo =
//                tournaments.value?.peekContent()?.firstOrNull { it.containsTournament(id) }
//
//            stringList.add(tournamentCombo?.getTournamentName(id) ?: "")
//        }
//
//        return stringList.joinToString(separator = "/")

        return ""
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