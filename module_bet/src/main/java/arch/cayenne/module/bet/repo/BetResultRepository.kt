package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import kotlinx.coroutines.CoroutineScope

class BetResultRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao
) : BaseRepository() {

    fun observeBetById(id: Long) = betDao.observeBetById(id)
    fun observeComboBet() = betDao.observeComboBet()
}