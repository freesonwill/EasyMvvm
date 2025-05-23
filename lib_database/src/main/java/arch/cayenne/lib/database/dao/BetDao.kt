package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import arch.cayenne.lib.database.entity.BetSelectionBean
import arch.cayenne.lib.database.entity.BetSelectionLiteBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetDao : BaseDao<BetBean>() {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelection(data: BetSelectionBean): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSelection(data: List<BetSelectionBean>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDetail(data: BetDetailBean): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDetail(data: List<BetDetailBean>): List<Long>

    @Query("SELECT * FROM BetBean WHERE status IN (:statuses) ORDER BY betId DESC LIMIT 1")
    abstract suspend fun getLastBetOrder(
        statuses: List<BetStatusEnum> = listOf(
            BetStatusEnum.COMPLETE,
            BetStatusEnum.BETTING
        )
    ): BetBean?

    @Query("SELECT * FROM BetBean WHERE status = :status ORDER BY betId DESC LIMIT 1")
    abstract suspend fun getCurrentBet(status: BetStatusEnum = BetStatusEnum.PENDING): BetBean?

    @Query("UPDATE BetBean SET status = :status WHERE betId = :betId")
    abstract suspend fun updateBetStatus(betId: Long, status: BetStatusEnum)

    @Query("UPDATE BetBean SET betType = :type WHERE betId = :betId AND status = :status")
    abstract suspend fun updateBetType(
        betId: Long,
        type: BetTypeEnum,
        status: BetStatusEnum = BetStatusEnum.PENDING
    )

    @Query("SELECT * FROM BetSelectionBean WHERE betId = :betId")
    abstract suspend fun getSelections(betId: Long): List<BetSelectionBean>

    @Query(
        "SELECT * FROM BetSelectionBean WHERE selectionId = :selectionId and betId = ( " +
                "        SELECT betId FROM BetBean" +
                "        WHERE status = :status" +
                "        ORDER BY betId DESC" +
                "        LIMIT 1" +
                "    )"
    )
    abstract suspend fun getCurrentSelectionById(
        selectionId: Long,
        status: BetStatusEnum = BetStatusEnum.PENDING
    ): BetSelectionBean?

    @Query("SELECT * FROM BetDetailBean WHERE betId = :betId")
    abstract suspend fun getDetail(betId: Long): List<BetDetailBean>

    @Query("SELECT * FROM BetDetailBean WHERE orderId = :orderId")
    abstract suspend fun getDetailByOrderId(orderId: String): BetDetailBean?

    @Query("SELECT * FROM BetSelectionBean WHERE betId = :betId")
    abstract fun observeSelections(betId: Long): Flow<List<BetSelectionBean>>

    @Query(
        "SELECT matchId, selectionId FROM BetSelectionBean WHERE betId = (" +
                "        SELECT betId FROM BetBean" +
                "        WHERE status = :status" +
                "        ORDER BY betId DESC" +
                "        LIMIT 1" +
                "    )"
    )
    abstract fun observeCurrentSelections(status: BetStatusEnum = BetStatusEnum.PENDING): Flow<List<BetSelectionLiteBean>>

    @Query(
        "SELECT selectionId FROM BetSelectionBean WHERE betId = (" +
                "        SELECT betId FROM BetBean" +
                "        WHERE status = :status" +
                "        ORDER BY betId DESC" +
                "        LIMIT 1" +
                "    ) and matchId = :matchId"
    )
    abstract fun observeCurrentSelectionsByMatchId(matchId: Long, status: BetStatusEnum = BetStatusEnum.PENDING): Flow<Long?>

    @Update
    abstract fun updateSelection(data: BetSelectionBean): Int

    @Query("SELECT * FROM BetDetailBean WHERE betId = :betId")
    abstract fun observeDetail(betId: Long): Flow<List<BetDetailBean>>

    @Query("UPDATE BetDetailBean SET status = :status WHERE orderId = :order")
    abstract suspend fun updateDetailResult(order: String, status: BetResultStatusEnum)

    @Query("SELECT COUNT(*) FROM BetSelectionBean WHERE betId = ( SELECT betId FROM BetBean WHERE status = :status AND betType = :type ORDER BY betId DESC LIMIT 1)")
    abstract fun observeComboCount(
        status: BetStatusEnum = BetStatusEnum.PENDING,
        type: BetTypeEnum = BetTypeEnum.COMBO
    ): Flow<Int>

    @Query("DELETE FROM BetBean WHERE betId = :betId")
    abstract suspend fun removeBet(betId: Long)

    @Query("DELETE FROM BetSelectionBean WHERE betId = :betId")
    abstract suspend fun removeBetSelection(betId: Long)

    @Query("DELETE FROM BETDETAILBEAN WHERE betId = :betId")
    abstract suspend fun removeBetDetail(betId: Long)

    suspend fun removeCurrentBet() {
        val betId = getCurrentBet()?.betId ?: return
        removeBet(betId)
        removeBetSelection(betId)
        removeBetDetail(betId)
    }

    @Query("DELETE FROM BetSelectionBean WHERE matchId = :matchId and betId = :betId")
    abstract suspend fun removeBetSelectionByMatchId(betId: Long, matchId: Long)

    @Query(
        "SELECT selectionId FROM BetSelectionBean WHERE betId = (" +
                "        SELECT betId FROM BetBean" +
                "        WHERE status = :status" +
                "        ORDER BY betId DESC" +
                "        LIMIT 1" +
                "    )"
    )
    abstract suspend fun getCurrentSelectionIds(status: BetStatusEnum = BetStatusEnum.PENDING): List<Long>
}