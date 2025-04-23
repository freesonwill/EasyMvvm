package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetStatusEnum
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetDao: BaseDao<BetBean>() {

    @Query("SELECT * FROM BetBean WHERE betType = :type LIMIT 1")
    abstract fun observeSingleBet(type: Int = BetTypeEnum.SINGLE.ordinal): Flow<BetBean?>

    @Query("SELECT * FROM BetBean WHERE matchId = :id LIMIT 1")
    abstract fun observeBetById(id: Long): Flow<BetBean?>

    @Query("SELECT * FROM BetBean")
    abstract suspend fun getBetSheet(): List<BetBean>

    @Query("SELECT * FROM BetBean WHERE matchId = :id")
    abstract suspend fun getBetById(id: Long): BetBean?

    @Query("SELECT COUNT(*) FROM BetBean WHERE betType = :type")
    abstract fun observeComboBetCount(type: Int = BetTypeEnum.COMBO.ordinal): Flow<Int>

    @Query("SELECT * FROM BetBean WHERE betType = :type")
    abstract fun getComboBet(type: Int = BetTypeEnum.COMBO.ordinal): List<BetBean>

    @Query("SELECT * FROM BetBean WHERE betType = :type")
    abstract fun observeComboBet(type: Int = BetTypeEnum.COMBO.ordinal): Flow<List<BetBean>>

    @Query("UPDATE BetBean SET betType = :type WHERE matchId = :id")
    abstract suspend fun updateBetType(id: Long, type: BetTypeEnum)

    @Query("UPDATE BetBean SET status = :status WHERE matchId = :id")
    abstract suspend fun updateBetStatus(id: Long, status: BetStatusEnum)

    @Query("UPDATE BetBean SET status = :status WHERE matchId IN (:ids)")
    abstract suspend fun updateBetListStatus(ids: List<Long>, status: BetStatusEnum)

    @Query("UPDATE BetBean SET reverseOdds = :reserveOdds WHERE matchId = :id")
    abstract suspend fun setReserveOdds(id: Long, reserveOdds: Int?)
    /**
     * 移除非roundId的投注記錄
     */
    @Query("delete from BetBean")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM BetBean WHERE matchId = :id")
    abstract suspend fun removeBet(id: Long)

}