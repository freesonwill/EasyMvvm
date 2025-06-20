package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import arch.cayenne.lib.database.entity.LiveMarketBean
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveSelectionBean
import arch.cayenne.lib.database.entity.LiveSelectionBeanRecord
import arch.cayenne.lib.database.entity.SelectionsEdit
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LiveMatchDao : BaseDao<LiveMatchBean>() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMatch(match: List<LiveMatchBean>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMarkets(markets: List<LiveMarketBean>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelections(selections: List<LiveSelectionBean>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelectionsRecord(selections: List<LiveSelectionBeanRecord>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelectionEdit(selections: List<SelectionsEdit>)

    @Transaction
    @Query("SELECT * FROM LiveMatchBean WHERE matchId = :matchId")
    abstract suspend fun getMatchById(matchId: Long): LiveMatchBean

    @Query("SELECT * FROM LiveMatchBean WHERE matchId = :matchId")
    abstract fun observeMatchById(matchId: Long): Flow<LiveMatchBean>

    @Query("SELECT * FROM LiveSelectionBean WHERE marketId IN (:marketIds)")
    abstract fun observeSelectionByIds(marketIds: List<Long>): Flow<List<LiveSelectionBean>>

    @Transaction
    @Query("SELECT * FROM LiveMatchBean WHERE matchId IN (:matchIds)")
    abstract suspend fun getMatchByIds(matchIds: List<Long>): List<LiveMatchBean>

    @Transaction
    @Query("SELECT * FROM LiveSelectionBean WHERE marketId = :marketId")
    abstract suspend fun getSelectionById(marketId: Long): LiveSelectionBean

    @Transaction
    @Query("SELECT * FROM LiveSelectionBean WHERE selectionId = :selectionId")
    abstract suspend fun getSelectionBySelectionId(selectionId: Long): LiveSelectionBean

    @Transaction
    @Query("SELECT * FROM LiveSelectionBean WHERE marketId =:marketId")
    abstract suspend fun getSelectionsByIds(marketId: Long): List<LiveSelectionBean>

    @Transaction
    @Query("SELECT * FROM SelectionsEdit")
    abstract suspend fun getSelectionsEdit(): List<SelectionsEdit>

    @Transaction
    @Query("SELECT * FROM LiveSelectionBeanRecord")
    abstract suspend fun getSelectionsRecord(): List<LiveSelectionBeanRecord>

    @Query("DELETE FROM LiveMatchBean")
    abstract fun deleteMatchBean()

    @Query("DELETE FROM LiveMarketBean")
    abstract fun deleteMarketBean()

    @Query("DELETE FROM LiveSelectionBean")
    abstract fun deleteSelectionBean()

    @Query("DELETE FROM LiveSelectionBeanRecord")
    abstract fun deleteSelectionBeanRecord()

    @Query("DELETE FROM SelectionsEdit")
    abstract fun deleteSelectionsEdit()


    @Transaction
    @Query("DELETE FROM LiveSelectionBean WHERE selectionId IN (:selectionsIds)")
    abstract fun deleteSelectionBeanById(selectionsIds: List<Long>)

    @Transaction
    @Query("DELETE FROM LiveSelectionBeanRecord WHERE selectionId IN (:selectionsIds)")
    abstract fun deleteSelectionBeanRecordById(selectionsIds: List<Long>)


    //收到notify更新数据
    @Query(
        "UPDATE LiveMatchBean " +
                "SET basic_status = :status, " +
                "basic_betStop = :betStop, " +
                "basic_startTime = :startTime," +
                "live_clock = :clock, " +
                "live_rollClock = :rollClock, " +
                "live_period = :period, " +
                "live_score = :score, " +
                "live_liveVideo = :liveVideo, " +
                "live_charRoom = :charRoom, " +
                "live_viewerCount = :viewerCount, " +
                "live_clockModified = :clockModified " +
                "WHERE matchId = :matchId"
    )
    abstract fun updateNotifyMatchInfo(
        matchId: Long,
        status: Int,
        betStop: Boolean,
        startTime: Long,
        clock: Int,
        rollClock: Boolean,
        period: String,
        score: String,
        liveVideo: Boolean,
        charRoom: Boolean,
        viewerCount: Int,
        clockModified: Long
    )


    @Transaction
    open suspend fun insertFullMatch(
        matches: List<LiveMatchBean>,
        markets: List<LiveMarketBean>,
        selections: List<LiveSelectionBean>,
        selectionsRecord: List<LiveSelectionBeanRecord>,
    ) {
        insertMatch(matches)
        insertMarkets(markets)
        insertSelections(selections)
        insertSelectionsRecord(selectionsRecord)
    }

    @Transaction
    open suspend fun updateLiveSelectionBean(
        selectionsEdit: List<LiveSelectionBean>,
        selectionsRecord: List<LiveSelectionBeanRecord>,
        selectionsAdd: List<LiveSelectionBean>,
        selectionsDeleteIds: List<Long>,
        marketsAdd: List<LiveMarketBean>,
        selectionsEditIds: List<SelectionsEdit>,
    ) {
        if (selectionsEditIds.isNotEmpty()) {
            insertSelectionEdit(selectionsEditIds)
        }
        if (selectionsEdit.isNotEmpty()) {
            insertSelections(selectionsEdit)
        }
        if (selectionsAdd.isNotEmpty()) {
            insertSelections(selectionsAdd)
        }
        if (marketsAdd.isNotEmpty()) {
            insertMarkets(marketsAdd)
        }
        if (selectionsRecord.isNotEmpty()) {
            insertSelectionsRecord(selectionsRecord)
        }
        if (selectionsEditIds.isNotEmpty()) {
            insertSelectionEdit(selectionsEditIds)
        }
        if (selectionsDeleteIds.isNotEmpty()) {
            deleteSelectionBeanById(selectionsDeleteIds)
            deleteSelectionBeanRecordById(selectionsDeleteIds)
        }
    }

    @Transaction
    open fun clearAllMatch() {
        deleteMatchBean()
        deleteMarketBean()
        deleteSelectionBean()
        deleteSelectionBeanRecord()
        deleteSelectionsEdit()
    }
}
