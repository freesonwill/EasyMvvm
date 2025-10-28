package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BetSlipData
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.lib.database.entity.BetSlipOrderHeaderBean
import arch.cayenne.module.betslip.data.repo.OrderSlipRepository
import arch.cayenne.module.order.data.constants.OrderSportPageEnum

class OrderSportPageViewModel(private val repo: OrderSlipRepository) : BaseViewModel() {

    private val _orderDataListener = MutableLiveData<List<BetSlipData>>()
    val orderDataListener: LiveData<List<BetSlipData>> = _orderDataListener

    fun setType(type: OrderSportPageEnum) {
        callApi({
            repo.getOrder(type.value, null, null, null, 10)
        }, { resp ->
            if (resp is ApiResponseState.Succeeded<*>) {
                val newData = mutableListOf<BetSlipData>()
                val data = resp.data as List<BetSlipOrderBean>
                val sumBet = data.sumOf { it.betAmount }
                val header = BetSlipOrderHeaderBean(
                    time = System.currentTimeMillis(),
                    currency = data.firstOrNull()?.currency ?: "CNY",
                    betAmount = sumBet,
                    validBetAmount = sumBet,
                )
                newData.add(header)
                newData.addAll(data)
                _orderDataListener.postValue(newData)
            }
        })
    }

}