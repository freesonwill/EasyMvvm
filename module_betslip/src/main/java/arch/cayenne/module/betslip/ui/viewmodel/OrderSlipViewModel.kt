package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipData
import arch.cayenne.module.betslip.data.repo.OrderSlipRepository
import arch.cayenne.module.betslip.utisl.BetSlipUtils.toBetSlipData
import kotlinx.coroutines.launch

open class OrderSlipViewModel(private val repo: OrderSlipRepository): BaseBetSlipViewModel() {

    //普通注单
    private val _orderLiveData = MutableLiveData<List<BetSlipData>>()
    val orderLiveData: LiveData<List<BetSlipData>> = _orderLiveData

    override fun refreshData(status: BetSlipEnum) {
        viewModelScope.launch {
            repo.getOrderReq(
                status.value,
                startTime,
                endTime,
                null,
                SIZE,
                sportId,
                matchId,
            )?.let { result ->
                _state.value = Event(if(result.isEmpty()) DynamicStateLayout.States.DATA_EMPTY else DynamicStateLayout.States.NULL)
                _orderLiveData.value = result.toBetSlipData()
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun loadMoreData(status: BetSlipEnum) {
        val list = _orderLiveData.value
        viewModelScope.launch {
            repo.getOrderReq(
                status.value,
                startTime,
                endTime,
                list?.lastOrNull()?.order?.betTime,
                SIZE,
                sportId,
                matchId,
            )?.let { result ->
                _state.value = Event(DynamicStateLayout.States.NULL)
                if (result.isNotEmpty()) {
                    val newList = mutableListOf<BetSlipData>()
                    val oldList = _orderLiveData.value ?: emptyList()
                    val resultList = result.toBetSlipData()
                    newList.addAll(oldList)
                    newList.addAll(resultList)
                    _orderLiveData.value = newList
                }
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun updateData(status: BetSlipEnum, betId: String) {
        val (index, previousItem) = _orderLiveData.value?.let { list ->
            val idx = list.indexOfFirst { it.order?.betId == betId }
            val prev = if (idx > 0) list[idx - 1] else null
            idx to prev
        } ?: return

        viewModelScope.launch {
            repo.getOrderReq(
                status.value,
                startTime,
                endTime,
                previousItem?.order?.betTime,
                1,
                sportId,
                matchId,
            )?.let { result ->
                val updatedItem = result.toBetSlipData().firstOrNull() ?: return@let
                val currentList = _orderLiveData.value?.toMutableList() ?: return@let
                if (index in currentList.indices) {
                    currentList[index] = updatedItem
                    _orderLiveData.value = currentList.toList() // 確保新 list 觸發 observer
                }
            } ?: run {
                _state.value = Event(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun canLoadMore(): Boolean {
        return !(_orderLiveData.value.isNullOrEmpty() || (_orderLiveData.value!!.size % SIZE != 0))
    }
}