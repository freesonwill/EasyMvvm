package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.InfoDao
import kotlinx.coroutines.CoroutineScope

class BalanceRepository(
    override val scope: CoroutineScope,
    private val infoDao: InfoDao
): BaseRepository() {

    fun observeBalance() = infoDao.observeBalance()

}