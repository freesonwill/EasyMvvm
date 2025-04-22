package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleBetRepository(
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeSingleBet() = betDao.observeSingleBet()

    fun removeBet(id: Long) {
        scope.launch {
            betDao.removeBet(id)
        }
    }

    fun saveToCombo(id: Long) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.COMBO)
        }
    }

    fun sendBet(id: Long, money: Long) {
        scope.launch {
            betDao.getBetById(id)?.let {
                if (it.betType == BetTypeEnum.SINGLE) {
                    betDao.updateBetStatus(id, BetStatusEnum.BETTING)
                    // TODO 等接入實際盤口資料後再測試
                    val resp = remoteManager.singleBet(scope, it, money)
                    if (resp == null || !resp.isSuccessful) {
                        betDao.updateBetStatus(id, BetStatusEnum.FAIL)
                    } else {
                        betDao.updateBetStatus(id, BetStatusEnum.COMPLETE)
                    }
                }

            }
        }
    }
}