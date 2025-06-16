package arch.cayenne.module.betslip.data.repo

import arch.cayenne.module.betslip.BetSlipRemoteManager
import arch.cayenne.module.betslip.data.model.BetSlipOrderBean
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
        sportIds: List<Int>,
        matchId: Long,
    ): List<BetSlipOrderBean>? {
        return withContext(scope.coroutineContext){
            remoteManager.getOrderReq(
                status,
                startTime,
                endTime,
                cursorBetTime,
                size,
                if (sportIds.size == 1 && sportIds.first() == -1) null else sportIds,
                if (matchId == -1L) null else matchId)
        }
    }
}