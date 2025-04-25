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

class ReserveRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val betResultDao: BetResultDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    fun observeReserveBet() = betDao.observeReserveBet()

    fun setSingleToReserve(id: Long) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.RESERVE)
        }
    }

    fun removeReserve(id: Long) {
        scope.launch {
            betDao.updateBetType(id, BetTypeEnum.SINGLE)
        }
    }

    suspend fun sendReserve(id: Long, odds: Int, money: Long) = withContext(scope.coroutineContext) {
        val bet = betDao.getBetById(id) ?: return@withContext null
        if (bet.betType != BetTypeEnum.RESERVE) return@withContext null

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

        launch {
            val resp = remoteManager.reserveBet(bet, odds, money)
            val status = if (resp?.isSuccessful == true) BetResultStatusEnum.SUCCESS_BET else BetResultStatusEnum.REJECT

            betResultDao.updateDetail(resultId, 1, "", status)
            betDao.updateBetStatus(id, BetStatusEnum.COMPLETE)
        }

        resultId
    }
}