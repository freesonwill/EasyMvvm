package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class BetResultRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao
) : BaseRepository() {

    fun observeLastBetOrder() = betDao.observeLastBetOrder()

    suspend fun getSelection(betId: Long) = withContext(scope.coroutineContext) {
        betDao.getSelections(betId)
    }

    fun observeDetail(betId: Long) = betDao.observeDetail(betId)

    suspend fun continueBet(): BetTypeEnum? = withContext(scope.coroutineContext) {
        val lastBet = betDao.getLastBetOrder() ?: return@withContext null

        val selectionList = betDao.getSelections(lastBet.betId)
        val detailList = betDao.getDetail(lastBet.betId)

        val newBet = BetBean(
            betType = lastBet.betType
        )
        val newBetId = betDao.insert(newBet)

        val newSelections = selectionList.map { selection ->
            selection.copy(betId = newBetId)
        }
        val newDetails = detailList.map { detail ->
            detail.copy(
                betId = newBetId,
                orderId = "",
                status = null
            )
        }

        betDao.insertSelection(newSelections)
        betDao.insertDetail(newDetails)

        newBet.betType
    }
}