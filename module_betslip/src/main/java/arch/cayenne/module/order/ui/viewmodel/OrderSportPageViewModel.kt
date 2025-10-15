package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.data.repo.OrderSlipRepository
import arch.cayenne.module.order.data.constants.OrderSportPageEnum

class OrderSportPageViewModel(private val repo: OrderSlipRepository) : BaseViewModel() {

    private val _orderDataListener = MutableLiveData<List<BetSlipOrderBean>>()
    val orderDataListener: LiveData<List<BetSlipOrderBean>> = _orderDataListener

    fun setType(type: OrderSportPageEnum) {
        callApi({
            repo.getOrder(type.value, null, null, null, 10)
        }, { resp ->
            if (resp is ApiResponseState.Succeeded<*>) {
                _orderDataListener.postValue(resp.data as List<BetSlipOrderBean>)
            }
        })
    }

}