package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.database.dao.BetDao
import arch.cayenne.lib.database.dao.MatchDao
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetLiteBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.SelectionLiteBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BetRepository(
    override val scope: CoroutineScope,
    private val betDao: BetDao, private val matchDao: MatchDao
) : BaseRepository() {

    val observerAllBet: Flow<List<BetLiteBean>> = flow {
        betDao.observeAllBet().collect {
            val data = it.map { bean ->
                BetLiteBean(
                    matchId = bean.matchId,
                    selectionId = bean.selectionLiteBean.id,
                )
            }
            emit(data)
        }
    }

    /***
     * 新增投注資料
     * @return type 返回單注or串關
     */
    suspend fun setSelection(matchId: Long, selectionId: Long) =
        withContext(scope.coroutineContext) {
            val isSingle = betDao.getBetSheet().isEmpty()
            val betBean = betDao.getBetById(matchId)
            val match = matchDao.getOneMatchById(matchId)
            val selections = matchDao.getSelectionById(selectionId)

            // 如果bet db無資料則新增，有資料則更新selection，相同selectionId則刪除
            if (betBean == null) {
                getSelectionLiteBean(match, selections)?.let { selectionLiteBean ->
                    val bean = BetBean(
                        matchId = matchId,
                        selectionLiteBean = selectionLiteBean,
                        betType = if (isSingle) BetTypeEnum.SINGLE else BetTypeEnum.COMBO,
                        leagueName = match.match.basicInfo.tournamentName,
                        matchName = match.match.basicInfo.matchName,
                        isBetStop = match.match.basicInfo.betStop,
                        isPlaying = match.match.basicInfo.status == 5,
                    )
                    betDao.insert(bean)
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

    private fun getSelectionLiteBean(
        match: MatchWithMarkets,
        selectionBean: SelectionBean
    ): SelectionLiteBean? {
        match.markets.find { market ->
            market.selections.find { it.selectionId == selectionBean.selectionId } != null
        }?.let { market ->
            val marketName = market.market.marketName
            return SelectionLiteBean(
                marketName = marketName,
                id = selectionBean.selectionId,
                name = selectionBean.name,
                odds = selectionBean.odds.getOdds(),
            )
        }
        return null
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
    private fun remove(matchId: Long) {
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