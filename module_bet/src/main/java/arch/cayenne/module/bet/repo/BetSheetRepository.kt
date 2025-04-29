package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.skin.SportSkinManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class BetSheetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val skinManager: SportSkinManager
): BaseRepository() {

    suspend fun getBetType() = withContext(scope.coroutineContext) {
        betDao.getCurrentBet()?.betType
    }

    fun getDayNightMode() {
        skinManager
    }
}