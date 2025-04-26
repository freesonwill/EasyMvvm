package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class BetResultRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao
) : BaseRepository() {

    suspend fun getLastOrderBet() = withContext(scope.coroutineContext) {
        betDao.getLastBetOrder()
    }

    suspend fun getSelection(betId: Long) = withContext(scope.coroutineContext) {
        betDao.getSelections(betId)
    }

    fun observeDetail(betId: Long) = betDao.observeDetail(betId)
}