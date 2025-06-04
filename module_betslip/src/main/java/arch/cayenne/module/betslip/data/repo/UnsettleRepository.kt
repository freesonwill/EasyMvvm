package arch.cayenne.module.betslip.data.repo

import arch.cayenne.module.betslip.BetSlipRemoteManager
import galaxy.client.proto.Client
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class UnsettleRepository(
    scope: CoroutineScope,
    remoteManager: BetSlipRemoteManager
): OrderSlipRepository(scope, remoteManager) {

    suspend fun earlySettle(
        betId: String,
        amount: String,
        expectPrice: String,
        acceptPriceReduce: Boolean
    ): Client.EarlySettleResp? {
        return withContext(scope.coroutineContext) {
            remoteManager.earlySettleReq(betId, amount, expectPrice, acceptPriceReduce)
        }
    }

    suspend fun earlySettledPrice(betId: String): List<Common.EarlySettlePrice>? {
        val resp = remoteManager.earlySettlePriceReq(betId)
        return withContext(scope.coroutineContext) {
            resp?.priceList
        }
    }
}