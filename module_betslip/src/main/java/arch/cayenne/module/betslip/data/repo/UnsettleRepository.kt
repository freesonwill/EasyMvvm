package arch.cayenne.module.betslip.data.repo

import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.constants.CommonExtension.toOrderBean
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnsettleRepository(
    scope: CoroutineScope,
    remoteManager: BetSlipRemoteManager
): OrderSlipRepository(scope, remoteManager) {

    private var notifyScope: Job? = null

    private val _observerEarlySettleNotify = MutableSharedFlow<BetSlipOrderBean>(replay = 1, extraBufferCapacity = 1)
    val observerEarlySettleNotify: Flow<BetSlipOrderBean> get() = _observerEarlySettleNotify

    suspend fun earlySettle(
        betId: String,
        amount: String,
        expectPrice: String,
        acceptPriceReduce: Boolean
    ): Client.EarlySettleResp? {
        return withContext(scope.coroutineContext) {
            remoteManager.earlySettleReq(betId, amount, expectPrice, acceptPriceReduce).apply {
                if (this?.success == true) {
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
                        val newData = res.data!!.order.toOrderBean()
                        _observerEarlySettleNotify.emit(newData)
                    }
                }
            }
        }
    }
}