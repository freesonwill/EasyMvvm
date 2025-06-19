package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.repo.OrderSlipRepository
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
    }

    override fun refreshData(status: BetSlipEnum) {
        if (type == null) {
            type = status
            repo.registerObserveOrderBean(status.value)
        }
        callApi({
            repo.getOrder(
                status.value,
                startTime,
                endTime,
                null,
                SIZE,
                sportIds,
                matchId,
            )
        })
    }

    override fun loadMoreData(status: BetSlipEnum) {
        val list = _orderLiveData.value
        callApi({
            repo.loadMoreOrder(
                status.value,
                startTime,
                endTime,
                list?.lastOrNull()?.betTime,
                SIZE,
                sportIds,
                matchId,
            )
        }, {
            if (it is ApiResponseState.Failed) {
                setState(DataState.NetworkUnavailable)
            }
        }, autoUpdateState = false)
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