package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetSelectionLiteBean
import arch.cayenne.module.bet.data.BetInsertBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext

class BetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao
) : BaseRepository() {

    companion object {
        private const val MAX_LIMIT_SIZE = 10
    }

    val observerAllBet: Flow<List<BetSelectionLiteBean>> = betDao.observeCurrentSelections()
    fun observerSelectionByMatchId(matchId: Long): Flow<Long?> =
        betDao.observeCurrentSelectionsByMatchId(matchId).distinctUntilChanged()

    /***
     * 新增投注資料
     * @return type 返回單注or串關
     */
    suspend fun setSelection(insertBean: BetInsertBean): AddSelectionStatus =
        withContext(scope.coroutineContext) {
            val bet = betDao.getCurrentBet()
            val betId = bet?.betId ?: betDao.insert(BetBean())

            val selections = betDao.getSelections(betId)
            val existing = selections.find { it.matchId == insertBean.matchId }

            if (existing?.selectionId == insertBean.selectionId) {
                betDao.removeBetSelectionByMatchId(betId, insertBean.matchId)
                checkBetBeanType(betId)
                return@withContext AddSelectionStatus.REMOVE
            }

            if (existing == null && selections.size >= MAX_LIMIT_SIZE) {
                return@withContext AddSelectionStatus.MAX_LIMIT
            }


            if (existing == null) {
                if (!insertBean.isParlay && selections.isNotEmpty()) {
                    return@withContext AddSelectionStatus.DISABLE_COMBO_FOR_PARLAY
                } else if (selections.isNotEmpty() && insertBean.provider != selections.first().provider) {
                    return@withContext AddSelectionStatus.DISABLE_COMBO_FOR_PROVIDER
                }

                val newBean = insertBean.toBetSelectionBean(betId)
                betDao.insertSelection(newBean)
                checkBetBeanType(betId)
                return@withContext if (selections.isEmpty()) AddSelectionStatus.SINGLE else AddSelectionStatus.COMBO
            } else {
                if (!insertBean.isParlay) {
                    return@withContext AddSelectionStatus.DISABLE_COMBO_FOR_PARLAY
                } else if (insertBean.provider != existing.provider) {
                    return@withContext AddSelectionStatus.DISABLE_COMBO_FOR_PROVIDER
                }
                val newBean = insertBean.toBetSelectionBean(betId)
                betDao.updateSelection(newBean)

                checkBetBeanType(betId)
                return@withContext AddSelectionStatus.UPDATE
            }
        }

    private suspend fun checkBetBeanType(betId: Long) {
        val selection = betDao.getSelections(betId)
        if (selection.isEmpty()) {
            betDao.removeCurrentBet()
        }
    }
}