package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReserveRepository(private val betDao: BetDao): BaseRepository() {
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
                    betDao.updateBetStatus(id, BetStatusEnum.BETTING)
                    delay(5_000L) // 模擬網路延遲
                    betDao.updateBetStatus(id, BetStatusEnum.COMPLETE)
                }
            }
        }
    }
}