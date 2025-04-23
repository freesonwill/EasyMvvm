package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BetResultRepository(private val betDao: BetDao): BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeBetById(id: Long) = betDao.observeBetById(id)
    fun observeComboBet() = betDao.observeComboBet()
}