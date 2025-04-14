package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
}