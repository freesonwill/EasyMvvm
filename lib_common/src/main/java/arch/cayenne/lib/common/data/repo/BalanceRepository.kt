package arch.cayenne.lib.common.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.InfoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map

class BalanceRepository(
    override val scope: CoroutineScope,
    private val infoDao: InfoDao
): BaseRepository() {
    fun observeBalance() = infoDao.observeBalance().map { it ?: 0L }

    fun observeCurrency() = infoDao.observeCurrency().map { it?: "" }

    fun observeInfo() = infoDao.observeInfo()

    suspend fun getBalance(): Long {
        return infoDao.getBalance()
    }

    suspend fun getCurrency(): String? {
        return infoDao.getCurrency()
    }
}