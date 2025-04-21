package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.SelectionLiteBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BetRepository(private val betDao: BetDao, private val matchDao: MatchDao): BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    /***
     * 新增投注資料
     * @return matchId 若返回參數，則為單注
     */
    suspend fun setSelection(matchId: Long, selectionId: Long) = withContext(scope.coroutineContext) {
        val isSingle = betDao.getBetSheet().isEmpty()
        val betBean = betDao.getBetById(matchId)
        val match = matchDao.getOneMatchById(matchId)
        val selections = matchDao.getSelectionById(selectionId)

        if (betBean == null) {
            scope.launch {
                getSelectionLiteBean(match, selections)?.let { selectionLiteBean ->
                    val bean = BetBean(
                        matchId = matchId,
                        selectionLiteBean = selectionLiteBean,
                        betType = if (isSingle) BetTypeEnum.SINGLE else BetTypeEnum.COMBO,
                        leagueName = match.match.basicInfo.tournamentName,
                        matchName = match.match.basicInfo.matchName,
                        // TODO : 這邊的最小金額跟最大金額需討論是否添加時取得
                        minAmount = 1000,
                        maxAmount = 100000,
                        isBetStop = match.match.basicInfo.betStop,
                        isPlaying = match.match.basicInfo.status == 5,
                    )
                    betDao.insert(bean)
                }
            }
        } else {
            if (betBean.selectionLiteBean.id == selectionId) {
                remove(matchId)
            } else {
                getSelectionLiteBean(match, selections)?.let { selectionLiteBean ->
                    betBean.selectionLiteBean = selectionLiteBean
                    betDao.update(betBean)
                }
            }
        }
        if (isSingle) {
            BetTypeEnum.SINGLE
        } else {
            BetTypeEnum.COMBO
        }
    }

    private fun getSelectionLiteBean(match: MatchWithMarkets, selectionBean: SelectionBean): SelectionLiteBean? {
        match.markets.find { market ->
            market.selections.find { it.selectionId == selectionBean.selectionId } != null
        }?.let { market ->
            val marketName = market.market.marketName
            return SelectionLiteBean(
                marketName = marketName,
                id = selectionBean.selectionId,
                name = selectionBean.name,
                odds = selectionBean.odds,
            )
        }
        return null
    }

    /***
     * 更新盤口投注限額
     */
    fun updateLimitAmount(matchId: Long, min: Long, max: Long) {
        scope.launch {
            betDao.getBetById(matchId)?.let { bet ->
                bet.minAmount = min
                bet.maxAmount = max
                betDao.update(bet)
            }
        }
    }

    /***
     * 暫時關閉盤口
     */
    fun closeSelection(matchId: Long) {
        scope.launch {
            betDao.getBetById(matchId)?.let { bet ->
                bet.isBetStop = true
                betDao.update(bet)
            }
        }
    }

    /***
     * 開啟盤口
     */
    fun openSelection(matchId: Long) {
        scope.launch {
            betDao.getBetById(matchId)?.let { bet ->
                bet.isBetStop = false
                betDao.update(bet)
            }
        }
    }

    /***
     * 刪除投注資料
     */
    fun remove(matchId: Long) {
        scope.launch {
            betDao.removeBet(matchId)
        }
    }

    /***
     * 滾球
     */
    fun setPlaying(matchId: Long) {
        scope.launch {
            betDao.getBetById(matchId)?.let { bet ->
                bet.isPlaying = true
                betDao.update(bet)
            }
        }
    }
}