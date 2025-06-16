package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BetSlipReserveBean
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.repo.ReserveSlipRepository
import kotlinx.coroutines.launch

class ReserveSlipViewModel(private val repo: ReserveSlipRepository): BaseBetSlipViewModel() {

    //预约注单
    private val _reserveLiveData = MutableLiveData<List<BetSlipReserveBean>>()
    val reserveLiveData: LiveData<List<BetSlipReserveBean>> = _reserveLiveData

    //取消预约
    private val _cancelReserveLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val cancelReserveLiveData: LiveData<Event<Boolean>> = _cancelReserveLiveData

    //修改赔率
    private val _modifyOddsLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val modifyOddsLiveData: LiveData<Event<Boolean>> = _modifyOddsLiveData

    init {
        viewModelScope.launch {
            repo.observeReserveBean().collect { reserveList ->
                _reserveLiveData.value = reserveList
                if (reserveList.isNotEmpty()) {
                    _state.value = Event(DynamicStateLayout.States.NULL)
                }
            }
        }
    }

    /**
     * 取消预约
     * */
    fun cancelReserve(order: BetSlipReserveBean) {
        viewModelScope.launch {
            val result = repo.reserveCancel(order.reserveId)
            _cancelReserveLiveData.value = Event(result?.success ?: false)
        }
    }

    /**
     * 修改预约
     * */
    fun modifyReserve(order: BetSlipReserveBean, newOdds: String) {
        viewModelScope.launch {
            val result = repo.reserveUpdate(order.reserveId, order.betAmount, newOdds)
            _modifyOddsLiveData.value = Event(result?.success ?: false)
        }
    }

    override fun refreshData(status: BetSlipEnum) {
        viewModelScope.launch {
            val resp = repo.getReserveOrder(
                startTime,
                endTime,
                sportIds,
                matchId,
                null,
                SIZE
            )
            if (resp == null) {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            } else if (resp.isEmpty()) {
                _state.value = Event(DynamicStateLayout.States.DATA_EMPTY)
            }
        }
    }

    override fun loadMoreData(status: BetSlipEnum) {
        val list = _reserveLiveData.value
        viewModelScope.launch {
            val resp = repo.loadMoreReserveOrder(
                startTime,
                endTime,
                sportIds,
                matchId,
                list?.lastOrNull()?.reserveTime,
                SIZE
            )
            if (resp == null) {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun updateData(status: BetSlipEnum, betId: String) {

    }

    override fun canLoadMore(): Boolean {
        return !(_reserveLiveData.value.isNullOrEmpty() || (_reserveLiveData.value!!.size % SIZE != 0))
    }
}