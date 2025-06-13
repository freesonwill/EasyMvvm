package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import arch.cayenne.module.betslip.data.repo.OrderSlipRepository
import arch.cayenne.module.betslip.utisl.BetSlipUtils.toBetSlipOrderData
import kotlinx.coroutines.launch

open class OrderSlipViewModel(private val repo: OrderSlipRepository): BaseBetSlipViewModel() {

    //普通注单
    protected val _orderLiveData = MutableLiveData<List<BetSlipOrderBean>>()
    val orderLiveData: LiveData<List<BetSlipOrderBean>> = _orderLiveData

    override fun refreshData(status: BetSlipEnum) {
        viewModelScope.launch {
            repo.getOrderReq(
                status.value,
                startTime,
                endTime,
                null,
                SIZE,
                sportIds,
                matchId,
            )?.let { result ->
                _state.value = Event(if(result.isEmpty()) DynamicStateLayout.States.DATA_EMPTY else DynamicStateLayout.States.NULL)
                _orderLiveData.value = result.toBetSlipOrderData()
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
                list?.lastOrNull()?.betTime,
                SIZE,
                sportIds,
                matchId,
            )?.let { result ->
                _state.value = Event(DynamicStateLayout.States.NULL)
                if (result.isNotEmpty()) {
                    val newList = mutableListOf<BetSlipOrderBean>()
                    val oldList = _orderLiveData.value ?: emptyList()
                    val resultList = result.toBetSlipOrderData()
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

    }

    override fun canLoadMore(): Boolean {
        return !(_orderLiveData.value.isNullOrEmpty() || (_orderLiveData.value!!.size % SIZE != 0))
    }
}