package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipReserve
import arch.cayenne.module.betslip.data.repo.ReserveSlipRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.launch

class ReserveSlipViewModel(private val repo: ReserveSlipRepository): BaseBetSlipViewModel() {

    //预约注单
    private val _reserveLiveData = MutableLiveData<List<BetSlipReserve>>()
    val reserveLiveData: LiveData<List<BetSlipReserve>> = _reserveLiveData

    //取消预约
    private val _cancelReserveLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val cancelReserveLiveData: LiveData<Event<Boolean>> = _cancelReserveLiveData

    //修改赔率
    private val _modifyOddsLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val modifyOddsLiveData: LiveData<Event<Boolean>> = _modifyOddsLiveData

    /**
     * 取消预约
     * */
    fun cancelReserve(order: Common.ReserveOrder) {
        viewModelScope.launch {
            val result = repo.reserveCancel(order.reserveId)?.apply {
                if (this.success) {
                    updateData(BetSlipEnum.Reserve, order.reserveId)
                }
            }
            _cancelReserveLiveData.value = Event(result?.success ?: false)
        }
    }

    /**
     * 修改预约
     * */
    fun modifyReserve(order: Common.ReserveOrder, odds: String) {
        viewModelScope.launch {
            val result = repo.reserveUpdate(order.reserveId, order.betAmount, odds)?.apply {
                if (this.success) {
                    updateData(BetSlipEnum.Reserve, order.reserveId)
                }
            }
            _modifyOddsLiveData.value = Event(result?.success ?: false)
        }
    }

    override fun refreshData(status: BetSlipEnum) {
        viewModelScope.launch {
            repo.getReserveOrder(
                startTime,
                endTime,
                sportId,
                matchId,
                null,
                SIZE
            )?.let { result ->
                _state.value = Event(if(result.isEmpty()) DynamicStateLayout.States.DATA_EMPTY else DynamicStateLayout.States.NULL)
                _reserveLiveData.value = result.map { BetSlipReserve(reserve = it) }.toList()
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun loadMoreData(status: BetSlipEnum) {
        val list = _reserveLiveData.value
        viewModelScope.launch {
            repo.getReserveOrder(
                startTime,
                endTime,
                sportId,
                matchId,
                list?.lastOrNull()?.reserve?.reserveTime,
                SIZE
            )?.let { result ->
                _state.value = Event(DynamicStateLayout.States.NULL)
                if (result.isNotEmpty()) {
                    val newList = mutableListOf<BetSlipReserve>()
                    val oldList = _reserveLiveData.value ?: emptyList()
                    val resultList = result.map { BetSlipReserve(reserve = it) }
                    newList.addAll(oldList)
                    newList.addAll(resultList)
                    _reserveLiveData.value = newList
                }
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun updateData(status: BetSlipEnum, betId: String) {
        val (index, previousItem) = _reserveLiveData.value?.let { list ->
            val idx = list.indexOfFirst { it.reserve.reserveId == betId }
            val prev = if (idx > 0) list[idx - 1] else null
            idx to prev
        } ?: return

        viewModelScope.launch {
            repo.getReserveOrder(
                startTime,
                endTime,
                sportId,
                matchId,
                previousItem?.reserve?.reserveTime,
                1,
            )?.let { result ->
                val updatedItem = result.map { BetSlipReserve(reserve = it) }.toList().firstOrNull() ?: return@let
                val currentList = _reserveLiveData.value?.toMutableList() ?: return@let
                if (index in currentList.indices) {
                    currentList[index] = updatedItem
                    _reserveLiveData.value = currentList.toList() // 確保新 list 觸發 observer
                }
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun canLoadMore(): Boolean {
        return !(_reserveLiveData.value.isNullOrEmpty() || (_reserveLiveData.value!!.size % SIZE != 0))
    }
}