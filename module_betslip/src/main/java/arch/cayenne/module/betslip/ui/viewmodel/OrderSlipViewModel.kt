package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.repo.OrderSlipRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class OrderSlipViewModel(private val repo: OrderSlipRepository): BaseBetSlipViewModel() {

    //普通注单
    private val _orderLiveData = MutableLiveData<List<BetSlipOrderBean>>()
    val orderLiveData: LiveData<List<BetSlipOrderBean>> = _orderLiveData

    private var type: BetSlipEnum? = null

    init {
        viewModelScope.launch {
            repo.observeOrderBeanFlow.collect {
                setData(it)
            }
        }
    }

    protected open fun setData(data: List<BetSlipOrderBean>) {
        _orderLiveData.value = data
        if (data.isEmpty()) {
            setState(DynamicStateLayout.States.DATA_EMPTY)
        } else {
            setState(DynamicStateLayout.States.NULL)
        }
    }

    override fun refreshData(status: BetSlipEnum) {
        if (type == null) {
            type = status
            repo.registerObserveOrderBean(status.value)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val resp = repo.getOrder(
                status.value,
                startTime,
                endTime,
                null,
                SIZE,
                sportIds,
                matchId,
            )
            if (resp == null) {
                setState(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun loadMoreData(status: BetSlipEnum) {
        val list = _orderLiveData.value
        viewModelScope.launch {
            val resp = repo.loadMoreOrder(
                status.value,
                startTime,
                endTime,
                list?.lastOrNull()?.betTime,
                SIZE,
                sportIds,
                matchId,
            )
            if (resp == null) {
                setState(DynamicStateLayout.States.NETWORK_ANOMALY)
            }
        }
    }

    override fun canLoadMore(): Boolean {
        return !(_orderLiveData.value.isNullOrEmpty() || (_orderLiveData.value!!.size % SIZE != 0))
    }

    override fun deleteAll() {
        type?.let {
            repo.deleteAll(it.value)
            type = null
        }
    }
}