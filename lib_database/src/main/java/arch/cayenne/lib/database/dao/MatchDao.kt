package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import arch.cayenne.lib.database.entity.MarketBean
import arch.cayenne.lib.database.entity.MarketBeanLite
import arch.cayenne.lib.database.entity.MarketSelectCrossRef
import arch.cayenne.lib.database.entity.MarketWithSelections
import arch.cayenne.lib.database.entity.MatchBean
import arch.cayenne.lib.database.entity.MatchBeanLite
import arch.cayenne.lib.database.entity.MatchMarketCrossRef
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.OldSelectionLite
import arch.cayenne.lib.database.entity.SelectionBean
import arch.cayenne.lib.database.entity.SelectionBeanLite
import arch.cayenne.lib.database.entity.TournamentMatchRef
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MatchDao : BaseDao<MatchBean>() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTournamentMatchRef(crossRef: List<TournamentMatchRef>) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertMatch(match: List<MatchBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertMarkets(markets: List<MarketBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertSelections(selections: List<SelectionBean>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertMatchMarketCrossRef(crossRef: List<MatchMarketCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMarketSelectionCrossRef(crossRef: List<MarketSelectCrossRef>)

    @Query("DELETE FROM MarketSelectCrossRef WHERE matchId IN (:matchId) AND marketId IN (:marketId) ")
    abstract suspend fun deleteMarketSelectionCrossRef(matchId: List<Long>, marketId:List<Long>)

    @Transaction
    @Query("SELECT * " +
            "FROM MatchBean bean " +
            "INNER JOIN TournamentMatchRef ref ON ref.playType = :playType AND ref.tournamentId = :tournamentId AND ref.date = :date " +
            "WHERE ref.matchId = bean.matchId ORDER BY ref.`order` DESC limit 1")
    abstract suspend fun queryLastMatch(playType: Int, tournamentId: Int, date: Long) : MatchBean?

    @Query("SELECT *" +
            "FROM TournamentMatchRef " +
            "WHERE playType = :playType AND tournamentId = :tournamentId  ORDER BY `order`")
    abstract fun observeMatchChange(playType: Int, tournamentId: Int): Flow<List<TournamentMatchRef>>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * " +
            "FROM MatchBean bean " +
            "INNER JOIN TournamentMatchRef ref ON ref.playType = :playType AND ref.tournamentId = :tournamentId AND ref.page = :page AND ref.date = :date " +
            "WHERE ref.matchId = bean.matchId ORDER BY ref.`order`")
    abstract suspend fun queryAllMatch(playType: Int, tournamentId: Int, page: Int, date: Long) : List<MatchBean>

    @Transaction
    @Query("delete " +
            "FROM TournamentMatchRef " +
            "WHERE playType = :playType AND tournamentId = :tournamentId AND date = :date"
    )
    abstract fun deleteCurrentTournamentMatchRef(playType: Int, tournamentId: Int, date: Long)

    @Transaction
    @Query("SELECT * " +
            "FROM MatchBean bean " +
            "INNER JOIN TournamentMatchRef ref ON ref.playType = :playType AND ref.tournamentId = :tournamentId AND ref.page = :page AND ref.date = :date " +
            "WHERE ref.matchId = bean.matchId")
    abstract fun observeAllMatch(playType: Int, tournamentId: Int, page: Int, date: Long) : Flow<List<MatchBean>>

    @Transaction
    @Query("SELECT * FROM MatchBean WHERE matchId = :matchId")
    abstract suspend fun getMatchById(matchId: Long) : MatchBean

    @Transaction
    @Query("SELECT matchId FROM MarketSelectCrossRef WHERE selectionId = :selectionId limit 1")
    abstract suspend fun getMatchIdBySelectionId(selectionId: Long) : Long?

    @Query("SELECT * FROM MatchBean WHERE matchId = :matchId")
    abstract fun observeMatchById(matchId: Long): Flow<MatchBean?>

    @Transaction
    @Query("SELECT * FROM MatchBean WHERE matchId IN (:matchIds)")
    abstract suspend fun getMatchByIds(matchIds: List<Long>) : List<MatchBean>

    @Transaction
    @Query("SELECT ref.matchId as ownerMatchId, market.marketId as marketId, market.marketName as marketName, market.status as status, ref.selectionCount as defaultSelectionCount " +
            "FROM MarketBean market " +
            "INNER JOIN  MatchMarketCrossRef ref ON ref.matchId = :matchId " +
            "WHERE market.marketId = ref.marketId ORDER BY ref.`index` ")
    abstract suspend fun getMarkets(matchId: Long): List<MarketBeanLite>

    @Transaction
    @Query("SELECT ref.matchId as ownerMatchId, market.marketId as marketId, market.marketName as marketName, market.status as status, ref.selectionCount as defaultSelectionCount " +
            "FROM MarketBean market " +
            "INNER JOIN MatchMarketCrossRef ref ON ref.matchId IN (:matchIds) " +
            "WHERE market.marketId = ref.marketId ORDER BY ref.matchId, ref.`index` ") // 按 matchId 排序有助於後續分組
    abstract suspend fun getMarketsForMatches(matchIds: List<Long>): List<MarketBeanLite>

    @Transaction
    @Query("SELECT sel.selectionId as selectionId, " +
                "ref.matchId as matchId,  " +
                "ref.marketId as marketId, " +
                "sel.detail_active as detailActive, " +
                "sel.name as name, " +
                "sel.shortName as shortName, " +
                "CASE WHEN :isEuropeOddsDisplay THEN sel.odds ELSE sel.odds - 100 END as odds, " +
                "sel.active as active, " +
                "sel.parlay as parlay, " +
                "0 as isSelected," +
                "0 as trend " +
            "FROM SelectionBean sel " +
            "INNER JOIN  MarketSelectCrossRef ref ON ref.matchId = :matchId AND ref.marketId = :marketId " +
            "WHERE sel.selectionId = ref.selectionId ORDER BY ref.`order`")
    abstract suspend fun getSelectionLites(matchId: Long, marketId: Long, isEuropeOddsDisplay: Boolean = true): List<SelectionBeanLite>

    @Transaction
    @Query("SELECT sel.selectionId as selectionId, " +
            "ref.matchId as matchId, " +
            "ref.marketId as marketId, " +
            "sel.detail_active as detailActive, " +
            "sel.name as name, " +
            "sel.shortName as shortName, " +
            "CASE WHEN :isEuropeOddsDisplay THEN sel.odds ELSE sel.odds - 100 END as odds, " +
            "sel.active as active, " +
            "sel.parlay as parlay, " +
            "0 as isSelected," +
            "0 as trend " +
            "FROM SelectionBean sel " +
            "INNER JOIN  MarketSelectCrossRef ref ON ref.matchId IN (:matchIds) " +
            "WHERE sel.selectionId = ref.selectionId ORDER BY ref.`order`")
    abstract suspend fun getSelectionLites(matchIds: List<Long>, isEuropeOddsDisplay: Boolean = true): List<SelectionBeanLite>

    @Transaction
    @Query("SELECT * FROM SelectionBean WHERE selectionId = :selectionId")
    abstract suspend fun getSelectionById(selectionId: Long): SelectionBean

    @Transaction
    @Query("SELECT bean.selectionId as selectionId, " +
            "CASE WHEN :isEuropeOddsDisplay THEN bean.odds ELSE bean.odds - 100 END as odds " +
            "FROM SelectionBean bean " +
            "WHERE selectionId IN (:selectionIds)")
    abstract suspend fun getOddSelectionsByIds(selectionIds: List<Long>, isEuropeOddsDisplay: Boolean): List<OldSelectionLite>

    @Query("DELETE FROM MatchBean" )
    abstract fun deleteMatchBean()

    @Query("DELETE FROM MatchBean WHERE matchId IN (:matchIds)")
    abstract fun deleteMatchBean(matchIds: List<Long>)

    @Query("DELETE FROM MarketBean" )
    abstract fun deleteMarketBean()

    @Query("DELETE FROM SelectionBean" )
    abstract fun deleteSelectionBean()

    @Query("DELETE FROM TournamentMatchRef" )
    abstract fun deleteTournamentMatchRef()

    @Query("DELETE FROM TournamentMatchRef WHERE matchId IN (:matchIds) " )
    abstract fun deleteTournamentMatchRef(matchIds: List<Long>)

    @Query("DELETE FROM MatchMarketCrossRef" )
    abstract fun deleteMatchMarketCrossRef()

    @Query("DELETE FROM MatchMarketCrossRef WHERE matchId IN (:matchIds) " )
    abstract fun deleteMatchMarketCrossRef(matchIds: List<Long>)

    @Query("DELETE FROM MarketSelectCrossRef" )
    abstract fun deleteMarketSelectCrossRef()

    @Query("DELETE FROM MarketSelectCrossRef WHERE matchId IN (:matchIds) " )
    abstract fun deleteMarketSelectCrossRef(matchIds: List<Long>)

    //收到notify時，match不是全收到，沒收到的那些也是不會變動的，所以只更新有收到的參數
    @Query("UPDATE MatchBean " +
            "SET basic_status = :status, " +
                "basic_betStop = :betStop, " +
                "basic_startTime = :startTime " +
            "WHERE matchId = :matchId")
    abstract fun updateNotifyMatchBasic(
        matchId: Long,
        status: Int,
        betStop: Boolean,
        startTime: Long,
    )
    @Query("UPDATE MatchBean " +
            "SET live_clock = :clock, " +
                "live_rollClock = :rollClock, " +
                "live_period = :period, " +
                "live_score = :score, " +
                "live_liveVideo = :liveVideo, " +
                "live_charRoom = :charRoom, " +
                "live_viewerCount = :viewerCount, " +
                "live_clockModified = :clockModified " +
            "WHERE matchId = :matchId")
    abstract fun updateNotifyMatchLive(
        matchId: Long,
        clock: Int,
        rollClock: Boolean,
        period: String,
        score: String,
        liveVideo: Boolean,
        charRoom: Boolean,
        viewerCount: Int,
        clockModified: Long
    )

    @Query(
        "UPDATE MatchBean SET collect = :collect WHERE matchId = :matchId")
    abstract fun updateOnlyMatchCollect(matchId: Long, collect: Boolean)

    @Transaction
    open suspend fun insertMatch(
        tournamentMatchRefs: List<TournamentMatchRef>,
        matches: List<MatchBean>,
        markets: List<MarketBean>,
        selections: List<SelectionBean>,
        marketCrossRef: List<MatchMarketCrossRef>,
        marketSelectCrossRefs: List<MarketSelectCrossRef>,
        playType: Int,
        tournamentId: Int,
        date: Long,
        isForce: Boolean,
    ) : List<Long> {
        if (isForce) {
            deleteCurrentTournamentMatchRef(playType, tournamentId, date)
        }
        return insertMatch(
            tournamentMatchRefs = tournamentMatchRefs,
            matches = matches,
            markets = markets,
            selections = selections,
            marketCrossRef = marketCrossRef,
            marketSelectCrossRefs = marketSelectCrossRefs
        )
    }

    @Transaction
    open suspend fun insertMatch(
        tournamentMatchRefs: List<TournamentMatchRef>,
        matches: List<MatchBean>,
        markets: List<MarketBean>,
        selections: List<SelectionBean>,
        marketCrossRef: List<MatchMarketCrossRef>,
        marketSelectCrossRefs: List<MarketSelectCrossRef>,
    ) : List<Long> {
        val ids = insertTournamentMatchRef(tournamentMatchRefs)
        insertMatch(matches, markets, selections, marketCrossRef, marketSelectCrossRefs)
        return ids
    }

    @Transaction
    open suspend fun insertMatch(
        matches: List<MatchBean>,
        markets: List<MarketBean>,
        selections: List<SelectionBean>,
        marketCrossRef: List<MatchMarketCrossRef>,
        marketSelectCrossRefs: List<MarketSelectCrossRef>,
    ) {
        insertMatch(matches)
        insertMarkets(markets)
        insertSelections(selections)
        insertMatchMarketCrossRef(marketCrossRef)
        //盤口的selection有可能在推播時整個變更（例如兩個選項+0.5/-0.5 -> +1/-1），所以刪除之前的cross ref，把之前盤口和selection連結斷開再連接，避免query取得之前的盤口
        deleteMarketSelectionCrossRef(marketCrossRef.map { it.matchId }, marketCrossRef.map { it.marketId })
        insertMarketSelectionCrossRef(marketSelectCrossRefs)
    }

    @Transaction
    open suspend fun updateOnlyMatch(
        updateIds: List<Long>,     //更新的賽事id
        matchLites: List<MatchBeanLite>,
        isEuropeOddsDisplay: Boolean,   //目前設定是否是歐洲盤
    ) : List<MatchWithMarkets>{
        matchLites.forEach { bean ->
            updateNotifyMatchBasic(
                matchId = bean.matchId,
                status = bean.status,
                betStop = bean.betStop,
                startTime = bean.startTime,

                )
            if (bean.liveInfo != null) {
                updateNotifyMatchLive(
                    matchId = bean.matchId,
                    clock = bean.liveInfo.clock,
                    rollClock = bean.liveInfo.rollClock,
                    period = bean.liveInfo.period,
                    score = bean.liveInfo.score,
                    liveVideo = bean.liveInfo.liveVideo,
                    charRoom = bean.liveInfo.charRoom,
                    viewerCount = bean.liveInfo.viewerCount,
                    clockModified = bean.liveInfo.clockModified,
                )
            }
        }
        return getOneMatchByIds(updateIds, isEuropeOddsDisplay)
    }

    @Transaction
    open suspend fun updateFullMatch(
        updateIds: List<Long>,     //更新的賽事id
        matchLites: List<MatchBeanLite>,
        markets: List<MarketBean>,
        selections: List<SelectionBean>,
        marketCrossRef: List<MatchMarketCrossRef>,
        marketSelectCrossRefs: List<MarketSelectCrossRef>,
        isEuropeOddsDisplay: Boolean,
    ): List<MatchWithMarkets> {
        matchLites.forEach { bean ->
            updateNotifyMatchBasic(
                matchId = bean.matchId,
                status = bean.status,
                betStop = bean.betStop,
                startTime = bean.startTime,

            )
            if (bean.liveInfo != null) {
                updateNotifyMatchLive(
                    matchId = bean.matchId,
                    clock = bean.liveInfo.clock,
                    rollClock = bean.liveInfo.rollClock,
                    period = bean.liveInfo.period,
                    score = bean.liveInfo.score,
                    liveVideo = bean.liveInfo.liveVideo,
                    charRoom = bean.liveInfo.charRoom,
                    viewerCount = bean.liveInfo.viewerCount,
                    clockModified = bean.liveInfo.clockModified,
                )
            }
        }
        val oldOdds = getOddSelectionsByIds(selections.map { it.selectionId }, isEuropeOddsDisplay)
        insertMarkets(markets)
        insertSelections(selections)
        insertMatchMarketCrossRef(marketCrossRef)
        //盤口的selection有可能在推播時整個變更（例如兩個選項+0.5/-0.5 -> +1/-1），所以刪除之前的cross ref，把之前盤口和selection連結斷開再連接，避免query取得之前的盤口
        deleteMarketSelectionCrossRef(marketCrossRef.map { it.matchId }, marketCrossRef.map { it.marketId })
        insertMarketSelectionCrossRef(marketSelectCrossRefs)
        return getOneMatchByIds(updateIds, isEuropeOddsDisplay).onEach {
            //加入賠率趨勢
            it.markets.forEach { markets ->
                markets.selections.forEach { selection ->
                    oldOdds.find {oldOdd ->  oldOdd.selectionId == selection.selectionId }?.apply {
                        selection.trend = selection.odds - this.odds
                    }
                }
            }
        }
    }
    //針對market id不同selection做些特殊處理
    private fun specialHandling(marketId: Long, originSelections: List<SelectionBeanLite>): List<SelectionBeanLite> {
//        return if (marketId == 1L && originSelections.size == 3) {
//            originSelections
//                .toMutableList()
//                .apply {
//                    this[1] = this[2].also { this[2] = this[1] }
//                }
//        } else { originSelections }
        return originSelections
    }

    @Transaction
    open suspend fun getOneMatchByIds(matchId: List<Long>, isEuropeOddsDisplay: Boolean): List<MatchWithMarkets> {
        val matchBeans = getMatchByIds(matchId)
        val marketBeans = getMarketsForMatches(matchId)
        val selectionBeans = getSelectionLites(matchId, isEuropeOddsDisplay)

        return matchBeans.map { matchBean ->
            val markets = marketBeans.filter { marketBean -> marketBean.ownerMatchId == matchBean.matchId }.map { marketBean ->
                val selections = selectionBeans.filter { selectionBean -> selectionBean.matchId == matchBean.matchId && selectionBean.marketId == marketBean.marketId }
                MarketWithSelections(marketBean, selections)
            }
            MatchWithMarkets(matchBean, markets)
        }
    }

    @Transaction
    open suspend fun getOneMatchById(matchId: Long, isEuropeOddsDisplay: Boolean): MatchWithMarkets {
        return getMatchById(matchId).let { matchBean ->
            val markets = getMarkets(matchBean.matchId).map { marketBean ->
                val selections = specialHandling(
                marketBean.marketId,
                getSelectionLites(matchBean.matchId, marketBean.marketId, isEuropeOddsDisplay)
            )
                MarketWithSelections(marketBean, selections)
            }
            MatchWithMarkets(matchBean, markets)
        }
    }

    @Transaction
    open fun deleteMissingMatch(ids: List<Long>) {
        deleteTournamentMatchRef(ids)
        deleteMatchBean(ids)
        deleteMatchMarketCrossRef(ids)
        deleteMarketSelectCrossRef(ids)
    }

    @Transaction
    open fun clearAllMatch() {
        deleteTournamentMatchRef()
        deleteMatchBean()
        deleteMarketBean()
        deleteMatchMarketCrossRef()
        deleteSelectionBean()
        deleteMarketSelectCrossRef()
    }
}
