package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import arch.cayenne.module.betslip.data.repo.UnsettleRepository
import galaxy.common.proto.Common
import kotlinx.coroutines.launch

class UnsettledViewModel(private val repo: UnsettleRepository): OrderSlipViewModel(repo) {

    //提前结算结果
    private val _earlySettledResultLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val earlySettledResultLiveData: LiveData<Event<Boolean>> = _earlySettledResultLiveData

    //检查提前结算
    private val _isSupportEarlySettleLiveData = MutableLiveData<Common.EarlySettlePrice>()
    val isSupportEarlySettleLiveData: LiveData<Common.EarlySettlePrice> = _isSupportEarlySettleLiveData

    //选择的提前结算注单
    var selectOrder: BetSlipOrderBean? = null
        private set

    init {
        viewModelScope.launch {
            repo.observerEarlySettleNotify.collect {
                updateEarlySettleData(it)
            }
        }
    }

    private fun updateEarlySettleData(newData: BetSlipOrderBean) {
        val currentList = orderLiveData.value ?: return

        val updatedList = currentList.mapNotNull { item ->
            if (item.id == newData.id) {
                // 如果金額相同，表示已提前結算完，移除項目
                if (newData.betAmount == newData.earlyBetAmount) {
                    null
                } else {
                    newData
                }
            } else {
                item
            }
        }
        setOrderData(updatedList)
    }

    /**
     * 部分提前结算
     * */
    fun earlyPartSettled(betId: String, money: String, expectPrice: String) {
        viewModelScope.launch {
            val result = repo.earlySettle(betId, money, expectPrice, false)?.apply {
                if (this.success) {
                    setDataToEarlySettling(betId)
                }
            }
            _earlySettledResultLiveData.value = Event(result?.success ?: false)
        }
    }

    /**
     * 检查是否支持提前结算
     * */
    fun isSupportEarlySettled(order: BetSlipOrderBean) {
        selectOrder = order
        viewModelScope.launch {
            val result = repo.earlySettledPrice(order.id)
            if (!result.isNullOrEmpty()) {
                _isSupportEarlySettleLiveData.value = result.first()
            }
        }
    }

    private fun setDataToEarlySettling(betId: String) {
        val currentList = orderLiveData.value ?: return

        val updatedList = currentList.map { item ->
            if (item.id == betId) {
                item.copy(
                    earlySettlePrice = item.earlySettlePrice.copy(
                        settleStatus = 102
                    )
                )
            } else {
                item
            }
        }
        setOrderData(updatedList)
    }
}