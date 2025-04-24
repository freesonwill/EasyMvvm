package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReserveRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    fun observeReserveBet() = betDao.observeReserveBet()

    suspend fun getReverseById(id: Long) = withContext(scope.coroutineContext) {
        val bet = betDao.getBetById(id)
        if (bet != null && bet.betType == BetTypeEnum.RESERVE) {
            bet
        } else {
            null
        }
    }

    fun setSingleToReserve(id: Long) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.RESERVE)
        }
    }

    fun removeReserve(id: Long) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }

    fun sendReserve(id: Long, money: Long) {
        scope.launch {
            betDao.getBetById(id)?.let {
                if (it.betType == BetTypeEnum.RESERVE) {
                    // TODO 等接入實際盤口資料後再測試
                    val resp = remoteManager.reserveBet(it, money)
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