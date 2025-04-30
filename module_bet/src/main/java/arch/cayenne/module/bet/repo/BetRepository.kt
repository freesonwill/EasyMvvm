package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetSelectionLiteBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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
    fun setSelection(matchId: Long, selectionId: Long) {
        scope.launch {
            val bet = betDao.getCurrentBet()
            // 如果bet db無資料則新增，有資料則更新selection，相同selectionId則刪除
            val betId = bet?.betId ?: betDao.insert(BetBean())
            val selections = betDao.getSelections(betId)
            val selection = selections.find { it.matchId == matchId }
            if (selection == null) {
                addSelection(betId, matchId, selectionId)
            } else {
                if (selection.selectionId == selectionId) {
                    betDao.removeBetSelectionByMatchId(betId, matchId)
                } else {
                    getSelectionLiteBean(betId, matchDao.getOneMatchById(matchId), matchDao.getSelectionById(selectionId))?.let { selectionLiteBean ->
                        betDao.updateSelection(selectionLiteBean)
                    }
                }
            }
            checkBetBeanType(betId)
        }
    }

    private suspend fun addSelection(betId: Long, matchId: Long, selectionId: Long) {
        val match = matchDao.getOneMatchById(matchId)
        val selection = matchDao.getSelectionById(selectionId)
        getSelectionLiteBean(betId, match, selection)?.let { selectionLiteBean ->
            betDao.insertSelection(selectionLiteBean)
        }
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
                isBetStop = selectionBean.active,
                isPlaying = match.match.basicInfo.status == 5,
                isParlay = selectionBean.parlay
            )
        }
        return null
    }

    /***
     * 暫時關閉盤口
     */
    fun closeSelection(selectionId: Long) {
        scope.launch {

        }
    }

    /***
     * 開啟盤口
     */
    fun openSelection(matchId: Long) {
        scope.launch {

        }
    }

    /***
     * 滾球
     */
    fun setPlaying(matchId: Long) {
        scope.launch {

        }
    }
}