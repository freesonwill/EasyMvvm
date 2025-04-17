package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReserveRepository(private val betDao: BetDao, private val remoteManager: BettingRemoteManager): BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getReverseById(id: Int) = withContext(scope.coroutineContext) {
        val bet = betDao.getBetById(id)
        if (bet != null && bet.betType == BetTypeEnum.RESERVE) {
            bet
        } else {
            null
        }
    }

    fun setSingleToReserve(id: Int, odds: Int) {
        scope.launch {
            betDao.setReserveOdds(id, odds)
            betDao.updateBetType(id, BetTypeEnum.RESERVE)
        }
    }

    fun removeReserve(id: Int) {
        scope.launch {
            betDao.setReserveOdds(id, null)
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }

    fun updateReserveOdds(id: Int, odds: Int?) {
        scope.launch {
            betDao.setReserveOdds(id, odds)
        }
    }

    fun sendReserve(id: Int, money: Int) {
        scope.launch {
            betDao.getBetById(id)?.let {
                if (it.betType == BetTypeEnum.RESERVE) {
                    // TODO 等接入實際盤口資料後再測試
                    val resp = remoteManager.reserveBet(scope, it, money)
                    if (resp == null || !resp.isSuccessful) {
                        betDao.updateBetStatus(id, BetStatusEnum.FAIL)
                    } else {
                        betDao.updateBetStatus(id, BetStatusEnum.COMPLETE)
                    }
                }
            }
        }
    }
}