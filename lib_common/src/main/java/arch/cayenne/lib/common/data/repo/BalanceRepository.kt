package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.InfoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class BalanceRepository(
    override val scope: CoroutineScope,
    private val infoDao: InfoDao
): BaseRepository() {

    suspend fun observeBalance(): Flow<Long> = infoDao.observeBalance()

    suspend fun getBalance(): Long {
        return infoDao.getBalance()
    }
}