package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.AddSelectionStatus
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetSelectionLiteBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.module.bet.data.BetInsertBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao,
    private val matchDao: MatchDao
) : BaseRepository() {

    val observerAllBet: Flow<List<BetSelectionLiteBean>> = betDao.observeCurrentSelections()

    /***
     * 新增投注資料
     * @return type 返回單注or串關
     */
    suspend fun setSelection(matchId: Long, selectionId: Long): AddSelectionStatus = withContext(scope.coroutineContext) {
        val bet = betDao.getCurrentBet()
        val betId = bet?.betId ?: betDao.insert(BetBean())

        val selections = betDao.getSelections(betId)
        val existing = selections.find { it.matchId == matchId }

        // 1. 同場次 selection 存在且相同 → 刪除
        if (existing?.selectionId == selectionId) {
            betDao.removeBetSelectionByMatchId(betId, matchId)
            checkBetBeanType(betId)
            return@withContext AddSelectionStatus.REMOVE
        }

        // 2. 該場次還沒加進去 → 新增
        if (existing == null) {
            val newSelection = matchDao.getSelectionById(selectionId)

            // 非讓分盤則無法加入組合單
            if (!newSelection.parlay) {
                return@withContext AddSelectionStatus.DISABLE_COMBO
            }

            val match = matchDao.getOneMatchById(matchId)
            getSelectionLiteBean(betId, match, newSelection)?.let {
                betDao.insertSelection(it)
            } ?: return@withContext AddSelectionStatus.FAIL

            checkBetBeanType(betId)
            return@withContext if (bet == null) AddSelectionStatus.SINGLE else AddSelectionStatus.COMBO
        }

        // 3. 同場次但不同 selection → 更新
        val match = matchDao.getOneMatchById(matchId)
        val newSelection = matchDao.getSelectionById(selectionId)
        getSelectionLiteBean(betId, match, newSelection)?.let {
            betDao.updateSelection(it)
        } ?: return@withContext AddSelectionStatus.FAIL

        checkBetBeanType(betId)
        return@withContext AddSelectionStatus.UPDATE
    }

    suspend fun setSelection(insertBean: BetInsertBean): AddSelectionStatus = withContext(scope.coroutineContext) {
        val bet = betDao.getCurrentBet()
        val betId = bet?.betId ?: betDao.insert(BetBean())

        val selections = betDao.getSelections(betId)
        val existing = selections.find { it.matchId == insertBean.matchId }

        // 1. 同場次 selection 存在且相同 → 刪除
        if (existing?.selectionId == insertBean.selectionId) {
            betDao.removeBetSelectionByMatchId(betId, insertBean.matchId)
            checkBetBeanType(betId)
            return@withContext AddSelectionStatus.REMOVE
        }

        // 2. 該場次還沒加進去 → 新增
        if (existing == null) {

            // 非讓分盤則無法加入組合單
            if (!insertBean.isParlay) {
                return@withContext AddSelectionStatus.DISABLE_COMBO
            }

            val newBean = insertBean.toBetSelectionBean(betId)
            betDao.insertSelection(newBean)
            checkBetBeanType(betId)
            return@withContext if (bet == null) AddSelectionStatus.SINGLE else AddSelectionStatus.COMBO
        }

        // 3. 同場次但不同 selection → 更新
        val newBean = insertBean.toBetSelectionBean(betId)
        betDao.updateSelection(newBean)

        checkBetBeanType(betId)
        return@withContext AddSelectionStatus.UPDATE
    }

    private suspend fun checkBetBeanType(betId: Long) {
        val selection = betDao.getSelections(betId)
        if (selection.isEmpty()) {
            betDao.removeBet(betId)
        } else if (selection.size == 1) {
            betDao.updateBetType(betId, BetTypeEnum.SINGLE)
        } else {
            betDao.updateBetType(betId, BetTypeEnum.COMBO)
        }
    }

    private fun getSelectionLiteBean(
        betId: Long,
        match: MatchWithMarkets,
        selectionBean: SelectionBean
    ): BetSelectionBean? {
        match.markets.find { market ->
            market.selections.find { it.selectionId == selectionBean.selectionId } != null
        }?.let { market ->
            return BetSelectionBean(
                betId = betId,
                matchId = match.match.matchId,
                marketName = market.market.marketName,
                selectionId = selectionBean.selectionId,
                name = selectionBean.name,
                odds = selectionBean.odds,
                leagueName = match.match.basicInfo.tournamentName,
                matchName = match.match.basicInfo.matchName,
                isActive = selectionBean.active,
                isPlaying = match.match.basicInfo.status == 5,
                isParlay = selectionBean.parlay
            )
        }
        return null
    }
}