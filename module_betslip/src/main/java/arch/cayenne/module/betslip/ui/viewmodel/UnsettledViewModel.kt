package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.model.OrderBean
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
    var selectOrder: OrderBean? = null
        private set

    /**
     * 部分提前结算
     * */
    fun earlyPartSettled(betId: String, money: String, expectPrice: String) {
        viewModelScope.launch {
            val result = repo.earlySettle(betId, money, expectPrice, false)?.apply {
                if (this.success) {
                    updateData(BetSlipEnum.UnSettled, betId)
                }
            }
            _earlySettledResultLiveData.value = Event(result?.success ?: false)
        }
    }

    /**
     * 检查是否支持提前结算
     * */
    fun isSupportEarlySettled(order: OrderBean) {
        selectOrder = order
        viewModelScope.launch {
            val result = repo.earlySettledPrice(order.betId)
            if (!result.isNullOrEmpty()) {
                _isSupportEarlySettleLiveData.value = result.first()
            }
        }
    }
}