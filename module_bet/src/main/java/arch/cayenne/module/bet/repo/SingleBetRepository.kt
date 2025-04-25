package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.BetResultDao
import arch.cayenne.lib.database.entity.BetResultDetailBean
import arch.cayenne.lib.database.entity.BetResultBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.module.bet.BettingRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    fun sendBet(id: Long, money: Long) {
        scope.launch {
            betDao.getBetById(id)?.let {
                if (it.betType == BetTypeEnum.SINGLE) {
                    betDao.updateBetStatus(id, BetStatusEnum.BETTING)
                    val resp = remoteManager.singleBet(it, money)
                    if (resp != null && resp.isSuccessful) {
                        val resultBean = BetResultDetailBean(
                            orderId = resp.orderId,
                            sumOdds = it.selectionLiteBean.odds.toOdds(),
                            inputMoney = money,
                            statusEnum = BetResultStatusEnum.getStatusByCode(resp.orderStatus)
                        )
                        val result = BetResultBean(
                            selectionIds = listOf(it.selectionLiteBean.id) ,
                            detail = listOf(resultBean)
                        )
                        betResultDao.insert(result)
                    }
                    betDao.updateBetStatus(id, BetStatusEnum.COMPLETE)

                }

            }
        }
    }
}