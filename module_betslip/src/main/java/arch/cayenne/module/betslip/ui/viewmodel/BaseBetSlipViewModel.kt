package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.repo.BaseBetSlipRepository


abstract class BaseBetSlipViewModel(private val baseRepo: BaseBetSlipRepository) : BaseViewModel() {

    companion object {
        const val SIZE = 10
    }

    private val _networkConnectedEvent = MutableLiveData<Event<DataState>>()
    val networkConnectedEvent: LiveData<Event<DataState>> get() = _networkConnectedEvent

    private var ids: Pair<Long, List<Int>> = Pair(-1, listOf(-1))
    protected val matchId: Long get() = ids.first
    protected val sportIds: List<Int> get() = ids.second

    private var times: Pair<Long?, Long?> = Pair(null, null)
    protected val startTime: Long? get() = times.first
    protected val endTime: Long? get() = times.second

    fun setIds(matchId: Long, sportId: Int) {
        this.ids = Pair(matchId, listOf(sportId))
    }

    fun setIds(matchId: Long, sportIds: List<Int>) {
        this.ids = Pair(matchId, sportIds)
    }

    fun setTime(startTime: Long?, endTime: Long?) {
        this.times = Pair(startTime, endTime)
    }

    abstract fun refreshData(status: BetSlipEnum)
    abstract fun loadMoreData(status: BetSlipEnum)
    abstract fun canLoadMore(): Boolean
    abstract fun deleteAll()

    fun checkNetwork(): Boolean {
        if (!baseRepo.isConnected) {
            _networkConnectedEvent.value = Event(DataState.NetworkUnavailable)
            return false
        }
        return true
    }
}