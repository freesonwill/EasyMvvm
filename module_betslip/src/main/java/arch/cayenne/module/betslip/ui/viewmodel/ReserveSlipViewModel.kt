package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.repo.ReserveSlipRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.launch

class ReserveSlipViewModel(private val repo: ReserveSlipRepository): BaseBetSlipViewModel() {

    //预约注单
    private val _reserveLiveData = MutableLiveData<List<BetSlipData>>()
    val reserveLiveData: LiveData<List<BetSlipData>> = _reserveLiveData

    //取消预约
    private val _cancelReserveLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val cancelReserveLiveData: LiveData<Boolean> = _cancelReserveLiveData

    //修改赔率
    private val _modifyOddsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val modifyOddsLiveData: LiveData<Boolean> = _modifyOddsLiveData

    /**
     * 获取注单预约单列表
     * */
    fun getReserveOrder() {
        viewModelScope.launch {
            repo.getReserveOrder(
                startTime,
                endTime,
                sportId,
                matchId,
                0L,
                SIZE
            )?.let { result ->
                _state.value = Event(if(result.isEmpty()) DynamicStateLayout.States.DATA_EMPTY else DynamicStateLayout.States.NULL)
                _reserveLiveData.value = result.map { BetSlipData(reserve = it) }.toList()
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    /**
     * 取消预约
     * */
    fun cancelReserve(order: Common.ReserveOrder) {
        viewModelScope.launch {
            val result = repo.reserveCancel(order.reserveId)
            _cancelReserveLiveData.value = result?.success ?: false
        }
    }

    /**
     * 修改预约
     * */
    fun modifyReserve(order: Common.ReserveOrder, odds: String) {
        viewModelScope.launch {
            val result = repo.reserveUpdate(order.reserveId, order.betAmount, odds)
            _modifyOddsLiveData.value = result?.success ?: false
        }
    }

    fun loadMoreReserve() {
        val list = _reserveLiveData.value
        viewModelScope.launch {
            repo.getReserveOrder(
                startTime,
                endTime,
                sportId,
                matchId,
                0L,
                SIZE
            )?.let { result ->
                _state.value = Event(DynamicStateLayout.States.NULL)
                if (result.isNotEmpty()) {
                    val newList = mutableListOf<BetSlipData>()
                    val oldList = _reserveLiveData.value ?: emptyList()
                    val resultList = result.map { BetSlipData(reserve = it) }
                    newList.addAll(oldList)
                    newList.addAll(resultList)
                    _reserveLiveData.value = newList
                }
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun loadData(status: BetSlipEnum) {
        getReserveOrder()
    }

    override fun canLoadMore(): Boolean {
        return !(_reserveLiveData.value.isNullOrEmpty() || (_reserveLiveData.value!!.size % SIZE != 0))
    }
}