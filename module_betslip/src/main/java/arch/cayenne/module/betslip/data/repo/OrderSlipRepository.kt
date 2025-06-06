package arch.cayenne.module.betslip.data.repo

import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.model.OrderBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

open class OrderSlipRepository(
    scope: CoroutineScope,
    remoteManager: BetSlipRemoteManager
) : BaseBetSlipRepository(scope, remoteManager) {

    suspend fun getOrderReq(
        status: Int,
        startTime: Long?,
        endTime: Long?,
        cursorBetTime: Long?,
        size: Int,
        sportId: Int,
        matchId: Long,
    ): List<OrderBean>? {
        return withContext(scope.coroutineContext){
            remoteManager.getOrderReq(
                status,
                startTime,
                endTime,
                cursorBetTime,
                size,
                if (sportId == -1) null else sportId,
                if (matchId == -1L) null else matchId)
        }
    }
}