package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BetRepository(private val betDao: BetDao): BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun insert(bean: BetBean) {
        scope.launch {
            betDao.insert(bean)
        }
    }
}