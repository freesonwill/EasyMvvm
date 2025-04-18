package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.StringExt.toValue
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.Selection
import arch.cayenne.module.bet.BettingRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SingleBetRepository(
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeSingleBet() = betDao.observeSingleBet()

    fun removeBet(id: Int) {
        scope.launch {
            betDao.removeBet(id)
        }
    }

    fun saveToCombo(id: Int) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.COMBO)
        }
    }

    // TODO 此為測試用！！之後會刪除  此為測試用！！之後會刪除  此為測試用！！之後會刪除
    fun addMockData() {
        scope.launch {
            val id = Random.nextInt()
            betDao.insert(
                BetBean(
                    matchId = id,
                    selection = Selection(
                        marketName = "讓分盤",
                        id = 212263384,
                        name = "長春亞泰 (+0.5)",
                        odds = "1.9".toValue()
                    ),
                    betType = BetTypeEnum.COMBO,
                    leagueName = "亞洲青年U19錦標賽A",
                    matchName = "長春亞泰 vs 廣州隊",
                    minAmount = 1000,
                    maxAmount = 1000000,
                )
            )
        }
    }

    fun sendBet(id: Int, money: Int) {
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