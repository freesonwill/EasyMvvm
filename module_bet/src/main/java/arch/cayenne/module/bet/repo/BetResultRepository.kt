package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.BetResultDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetResultDetailBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class BetResultRepository(
    override val scope: CoroutineScope,
    private val betResultDao: BetResultDao,
    private val betDao: BetDao
) : BaseRepository() {

    suspend fun getBets(id: Long) = withContext(scope.coroutineContext) {
        return@withContext mutableListOf<BetBean>().apply {
            betResultDao.getBetResultById(id)?.let { betResult ->
                betResult.selectionIds.forEach { selectionId ->
                    betDao.getBetBySelectionId(selectionId)?.let { bet ->
                        add(bet)
                    }
                }
            }
        }
    }

    suspend fun getResultDetail(id: Long)= withContext(scope.coroutineContext) {
        return@withContext mutableListOf<BetResultDetailBean>().apply {
            betResultDao.getBetResultById(id)?.let { betResult ->
                addAll(betResult.detail)
            }
        }
    }
}