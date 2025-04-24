package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class FloatingButtonRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao
) : BaseRepository() {

    fun observeComboBetCount() = betDao.observeComboBetCount()

    suspend fun getSingleBetId() = with(scope.coroutineContext) {
        betDao.getBetSheet().first().matchId
    }

    fun saveToSingleBet(id: Long) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }
}