package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.database.dao.BetSlipOrderDao
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ConfirmingSlipRepository(
    scope: CoroutineScope,
    betSlipOrderDao: BetSlipOrderDao,
    remoteManager: BetSlipRemoteManager
): OrderSlipRepository(scope, betSlipOrderDao, remoteManager) {

    private var orderStatusJob: Job? = null

    fun registerOrderStatus() {
        if (orderStatusJob == null) {
            orderStatusJob = scope.launch {
                remoteManager.registerOrderStatus().collect { res ->
                    if (res.error == null && res.data != null) {
                        launch {
                            res.data!!.orderStatusList.forEach { status ->
                                betSlipOrderDao.getOrderBeanById(status.orderId)?.let { bean ->
                                    bean.earlySupport = status.status == 4
                                    bean.betSlipType = if (status.status == 4) BetSlipEnum.UnSettled.value else BetSlipEnum.Invalid.value
                                    betSlipOrderDao.update(bean)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun unregisterOrderStatus() {
        orderStatusJob?.cancel()
        orderStatusJob = null
    }
}