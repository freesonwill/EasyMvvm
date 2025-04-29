package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class FloatingButtonRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao
) : BaseRepository() {

    fun observeComboBetCount() = betDao.observeComboCount()

    fun saveToSingleBet() {
        scope.launch {
            betDao.getCurrentBet()?.let {
                betDao.updateBetType(it.betId, BetTypeEnum.SINGLE)
            }
        }
    }
}