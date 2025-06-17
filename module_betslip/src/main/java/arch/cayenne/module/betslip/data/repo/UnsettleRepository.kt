package arch.cayenne.module.betslip.data.repo

import arch.cayenne.lib.database.dao.BetSlipOrderDao
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.BetSlipEnum
import arch.cayenne.module.betslip.data.constants.CommonExtension.toOrderBean
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnsettleRepository(
    scope: CoroutineScope,
    betSlipOrderDao: BetSlipOrderDao,
    remoteManager: BetSlipRemoteManager
): OrderSlipRepository(scope, betSlipOrderDao, remoteManager) {

    private var notifyScope: Job? = null

    suspend fun earlySettle(
        betId: String,
        amount: String,
        expectPrice: String,
        acceptPriceReduce: Boolean
    ): Client.EarlySettleResp? {
        return withContext(scope.coroutineContext) {
            remoteManager.earlySettleReq(betId, amount, expectPrice, acceptPriceReduce).apply {
                if (this?.success == true) {
                    betSlipOrderDao.updateToPendingEarlySettle(betId)
                    registerEarlySettleNotify()
                }
            }
        }
    }

    suspend fun earlySettledPrice(betId: String): List<Common.EarlySettlePrice>? {
        val resp = remoteManager.earlySettlePriceReq(betId)
        return withContext(scope.coroutineContext) {
            resp?.priceList
        }
    }

    private fun registerEarlySettleNotify() {
        if (notifyScope == null) {
            notifyScope = scope.launch {
                remoteManager.registerEarlySettleNotify().collect { res ->
                    if (res.error == null && res.data != null) {
                        val newData = res.data!!.order.toOrderBean(BetSlipEnum.UnSettled.value)
                        updateEarlySettleData(newData)
                    }
                }
            }
        }
    }

    private fun updateEarlySettleData(newData: BetSlipOrderBean) {
        scope.launch {
            if (newData.betAmount == newData.earlyBetAmount) {
                betSlipOrderDao.updateBetSlipType(newData.betId, BetSlipEnum.Settled.value)
            } else {
                betSlipOrderDao.insert(newData)
            }
        }
    }
}