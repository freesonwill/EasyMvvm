package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FloatingButtonRepository(private val betDao: BetDao): BaseRepository() {

    fun observeComboBetCount() = betDao.observeComboBetCount()

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getSingleBetId() = with(scope.coroutineContext) {
        betDao.getBetSheet().first().gameId
    }

    fun saveToSingleBet(id: Int) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }
}