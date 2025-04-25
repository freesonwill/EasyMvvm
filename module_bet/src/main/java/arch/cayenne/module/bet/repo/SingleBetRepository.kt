package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.BetResultDao
import arch.cayenne.lib.database.entity.BetResultBean
import arch.cayenne.lib.database.entity.BetResultDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleBetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val betResultDao: BetResultDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    init {
        scope.launch {
            betDao.getSingleBet()?.let { bet ->
                remoteManager.getSingleRisk(bet.matchId, bet.selectionLiteBean.id)
                    ?.let { risk ->
                        if (risk.matchId == bet.matchId && risk.selectionId == bet.selectionLiteBean.id) {
                            bet.minAmount = risk.minAmount
                            bet.maxAmount = risk.maxAmount
                            betDao.update(bet)
                        }
                    }
            }
        }
    }

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

    suspend fun sendBet(id: Long, money: Long): Long? = withContext(scope.coroutineContext) {
        val bet = betDao.getBetById(id) ?: return@withContext null

        if (bet.betType != BetTypeEnum.SINGLE) return@withContext null

        betDao.updateBetStatus(id, BetStatusEnum.BETTING)


        val result = BetResultBean(
            selectionIds = listOf(bet.selectionLiteBean.id)
        )

        val resultId = betResultDao.insert(result)
        val resultBean = BetResultDetailBean(
            betResultId = resultId,
            sumOdds = bet.selectionLiteBean.odds.toOdds(),
            inputMoney = money
        )
        betResultDao.insertDetail(resultBean)
        // 🔄 非同步執行 singleBet，更新資料庫
        launch {
            val resp = remoteManager.singleBet(bet, money)
            if (resp != null && resp.isSuccessful) {
                betResultDao.updateDetail(resultId, 1, resp.orderId, BetResultStatusEnum.getStatusByCode(resp.orderStatus))
            } else {
                betResultDao.updateDetail(resultId, 1, "", BetResultStatusEnum.REJECT)
            }
            betDao.updateBetStatus(id, BetStatusEnum.COMPLETE)
        }

        resultId
    }

}