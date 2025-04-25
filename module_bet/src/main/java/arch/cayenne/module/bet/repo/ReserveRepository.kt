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
        var resultId: Long? = null
        betDao.getBetById(id)?.let {
            if (it.betType == BetTypeEnum.RESERVE) {
                val resp = remoteManager.reserveBet(it, odds, money)
                if (resp != null) {
                    val resultBean = BetResultDetailBean(
                        orderId = "",
                        sumOdds = it.selectionLiteBean.odds.toOdds(),
                        inputMoney = money,
                        statusEnum = if (resp.isSuccessful) BetResultStatusEnum.SUCCESS_BET else BetResultStatusEnum.REJECT
                    )
                    val result = BetResultBean(
                        selectionIds = listOf(it.selectionLiteBean.id) ,
                        detail = listOf(resultBean)
                    )
                    betResultDao.insert(result)
                    resultId = result.id
                }
                betDao.updateBetStatus(id, BetStatusEnum.COMPLETE)
            }
        }
        resultId
    }
}