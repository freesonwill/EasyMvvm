package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetSelectionLiteBean
import arch.cayenne.module.bet.BettingRemoteManager
import arch.cayenne.module.bet.data.BetInsertBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val remoteManager: BettingRemoteManager
) : BaseRepository() {

    companion object {
        private const val MAX_LIMIT_SIZE = 10
    }

    val observerAllBet: Flow<List<BetSelectionLiteBean>> = betDao.observeCurrentLiteSelections()
    fun observerSelectionByMatchId(matchId: Long): Flow<Long?> =
        betDao.observeCurrentSelectionsByMatchId(matchId).distinctUntilChanged()

    val isConnected: Boolean
        get() = remoteManager.isConnected

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

            val liteBean = BetSelectionLiteBean(
                matchId = insertBean.matchId,
                selectionId = insertBean.selectionId
            )

            if (existing?.selectionId == insertBean.selectionId) {
                scope.launch {
                    betDao.removeBetSelectionByMatchId(betId, insertBean.matchId)
                    checkBetBeanType(betId)
                }
                return@withContext AddSelectionStatus.Others.Remove
            }

            if (existing == null && selections.size >= MAX_LIMIT_SIZE) {
                return@withContext AddSelectionStatus.Failure.MaxLimit
            }


            if (existing == null) {
                if (!insertBean.isParlay && selections.isNotEmpty()) {
                    return@withContext AddSelectionStatus.Failure.DisableComboForParlay
                } else if (selections.isNotEmpty() && insertBean.provider != selections.first().provider) {
                    return@withContext AddSelectionStatus.Failure.DisableComboForProvider
                }
                scope.launch {
                    val newBean = insertBean.toBetSelectionBean(betId)
                    betDao.insertSelection(newBean)
                    checkBetBeanType(betId)
                }
                return@withContext if (selections.isEmpty()) {
                    AddSelectionStatus.Success.Single
                } else {
                    AddSelectionStatus.Success.Combo
                }
            } else {
                if (!insertBean.isParlay) {
                    return@withContext AddSelectionStatus.Failure.DisableComboForParlay
                } else if (insertBean.provider != existing.provider) {
                    return@withContext AddSelectionStatus.Failure.DisableComboForProvider
                }
                scope.launch {
                    val newBean = insertBean.toBetSelectionBean(betId)
                    betDao.updateSelection(newBean)

                    checkBetBeanType(betId)
                }
                return@withContext AddSelectionStatus.Success.Update(
                    existing.selectionId,
                    liteBean.selectionId
                )
            }
        }

    private suspend fun checkBetBeanType(betId: Long) {
        val selection = betDao.getSelections(betId)
        if (selection.isEmpty()) {
            betDao.removeCurrentBet()
        }
    }
}