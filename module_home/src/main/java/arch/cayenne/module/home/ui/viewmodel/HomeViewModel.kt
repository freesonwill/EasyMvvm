package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.data.repo.BalanceRepository
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.skin.LanguageManager
import arch.cayenne.module.home.data.constants.HomeState
import arch.cayenne.module.home.data.constants.PlayType
import arch.cayenne.module.home.data.constants.getPlayTypeById
import arch.cayenne.module.home.data.repo.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class HomeViewModel : BaseViewModel() {
    companion object {
        const val TOURNAMENT_ALL_ID = 0
        const val DEFAULT_DATE = -1L  //預設值，如果任何livedata收到這個預設值可以先略過要做的事情，主要拿來避免頁面生成時拿到舊的date
    }

    private val _currentPlayTypeId: MutableStateFlow<Int> = MutableStateFlow(PlayType.TODAY.id)
    val currentPlayTypeId: Int
        get() = _currentPlayTypeId.value
    private val _playTypeIndexChange: MutableLiveData<Event<Int>> = MutableLiveData<Event<Int>>()
    val playTypeIndexChange: LiveData<Event<Int>> = _playTypeIndexChange


    private val _currentSportId: MutableStateFlow<Int> = MutableStateFlow(SportEnum.Default.id)
    val currentSportId: Int
        get() = _currentSportId.value

    private val repository: HomeRepository by inject()
    private val balanceRepository: BalanceRepository by inject()
    val currentBalanceChange by lazy { MutableLiveData<InfoBean?>() }


    private var timerJob: Job? = null
    private val _timer = MutableLiveData<Event<Long>>()
    val timer: LiveData<Event<Long>> = _timer

    private val _notifyToChampion = MutableLiveData<Event<Unit>>()
    val notifyToChampion: LiveData<Event<Unit>> = _notifyToChampion

    //监听语言切换，对没有设置自动切换语言的view及时更新
    val languageManager: LanguageManager by inject()

    private val _notifySubHomeRefresh = MutableLiveData<Event<Unit>>()
    val notifySubHomeRefresh: LiveData<Event<Unit>> = _notifySubHomeRefresh

    val playTypeClickRecord = hashMapOf<Int, Long>()  //HashMap<PlayTypeId, RecordTime>

    //分类列表触发广告位收起动画  true 为收起 false 为展开
    private val _scroll = MutableLiveData<Boolean>()
    val scroll: LiveData<Boolean> = _scroll


    init {
        viewModelScope.launch {
            _currentPlayTypeId.collect {
                when (it) {
                    PlayType.TODAY.id -> _playTypeIndexChange.value = Event(0)
                    PlayType.EARLY.id -> _playTypeIndexChange.value = Event(1)
                    PlayType.CHAMPION.id -> _playTypeIndexChange.value = Event(2)
                    else -> Unit
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.observeLanguageChange().collect {
                resetAll()
            }
        }
    }


    override fun initViewModel() {
        super.initViewModel()
        "HomeViewModel initViewModel".logd(this::class.java.simpleName)
        //觀察餘額變化
        viewModelScope.launch(Dispatchers.IO) {
            balanceRepository.observeInfo().collect {
                withContext(Dispatchers.Main) {
                    currentBalanceChange.value = it
                }
            }
        }
        startTimer()
    }

    @Transaction
    private suspend fun resetAll() {
        repository.clearAllCache()
        withContext(Dispatchers.Main) {
//            setCurrentPlayType(PlayType.TODAY.id)
            _notifySubHomeRefresh.value = Event(Unit)
        }
    }

    //切換當前的一級選項(今日、早盤、冠軍)
    fun setCurrentPlayType(playTypeId: Int) {
        _currentPlayTypeId.value = playTypeId
        setState(HomeState.PlayTypeClick)
    }

    //提供子fragment透過shared HomeViewModel來告知HomeFragment該fragment的狀態
    fun changeState(state: DataState) {
        setState(state)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            var count = 0L
            while (isActive) {
                delay(1000L)
                launch(Dispatchers.Main) {
                    _timer.value = Event(count++)
                }
            }
        }
    }

    /**
     * @return 是否超過時間，需要重新整理賽事資料
     * */
    fun resetPageSelectedTimestamp(playTypeId: Int): Boolean {
        val refreshInternal = playTypeId.getPlayTypeById().refreshInterval
        val pageSelectedTimestamp = playTypeClickRecord[playTypeId] ?: 0L
        val isNeedRefresh =
            pageSelectedTimestamp != 0L && System.currentTimeMillis() - pageSelectedTimestamp > refreshInternal
        "currentPlayTypeId = ${playTypeId}  isNeedRefresh = $isNeedRefresh  pageSelectedTimestamp = ${pageSelectedTimestamp}".logi()
        playTypeClickRecord[playTypeId] = System.currentTimeMillis()
        return isNeedRefresh
    }

    fun setScroll(bool: Boolean) {
        if (bool != scroll.value) {
            _scroll.value = bool
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}