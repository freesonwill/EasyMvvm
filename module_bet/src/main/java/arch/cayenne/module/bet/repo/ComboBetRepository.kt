package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import arch.cayenne.module.bet.data.ComboMultiBetBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComboBetRepository(
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeComboBet() = betDao.observeComboBet()

    fun removeBet(id: Int) {
        scope.launch {
            betDao.removeBet(id)
        }
    }

    fun removeAll() {
        scope.launch {
            betDao.deleteAll()
        }
    }

    fun saveToSingleBet(id: Int) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }

    fun sendBet(multiBet: List<ComboMultiBetBean>) {
        scope.launch {
            val betBeans = betDao.getComboBet()
            val ids = betBeans.map { it.matchId }
            betDao.updateBetListStatus(ids, BetStatusEnum.BETTING)
            // TODO 等接入實際盤口資料後再測試
            val resp = remoteManager.comboBet(scope, betBeans, multiBet)
            if (resp == null || !resp.isSuccessful) {
                betDao.updateBetListStatus(ids, BetStatusEnum.FAIL)
            } else {
                betDao.updateBetListStatus(ids, BetStatusEnum.COMPLETE)
            }
        }
    }
}