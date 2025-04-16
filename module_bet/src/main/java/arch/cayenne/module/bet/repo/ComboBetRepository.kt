package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ComboBetRepository(private val betDao: BetDao) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeComboBet() = betDao.observeComboBet()

    fun removeBet(id: Int) {
        scope.launch {
            betDao.removeBet(id)
        }
    }

    fun removeAll() {
        scope.launch {
            betDao.deleteAll()
        }
    }

    fun saveToSingleBet(id: Int) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }

    fun sendBet(ids: List<Int>) {
        // TODO 需再確認串關下注後台邏輯
        scope.launch {
            ids.forEach { id ->
                betDao.getBetById(id)?.let {
                    if (it.betType == BetTypeEnum.COMBO) {
                        betDao.updateBetStatus(id, BetStatusEnum.BETTING)
                        delay(5_000L) // 模擬網路延遲
                        betDao.updateBetStatus(id, BetStatusEnum.COMPLETE)
                    }
                }
            }
        }
    }
}