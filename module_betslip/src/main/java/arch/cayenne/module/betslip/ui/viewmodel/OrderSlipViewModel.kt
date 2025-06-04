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

    /**
     * 获取注单列表
     * */
    fun getOrders(status: BetSlipEnum) {
        viewModelScope.launch {
            repo.getOrderReq(
                status.value,
                startTime,
                endTime,
                0L,
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

    fun refreshOrder(status: BetSlipEnum) {
        getOrders(status)
    }

    fun loadMoreOrder(status: BetSlipEnum) {
        val list = _orderLiveData.value
        viewModelScope.launch {
            repo.getOrderReq(
                status.value,
                startTime,
                endTime,
                0L,
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

    override fun loadData(status: BetSlipEnum) {
        getOrders(status)
    }

    override fun canLoadMore(): Boolean {
        return !(_orderLiveData.value.isNullOrEmpty() || (_orderLiveData.value!!.size % SIZE != 0))
    }
}