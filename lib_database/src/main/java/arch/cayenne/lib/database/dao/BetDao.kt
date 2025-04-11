package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetBean
import arch.cayenne.lib.database.entity.BetTypeEnum
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetDao: BaseDao<BetBean>() {

    @Query("SELECT * FROM BetBean WHERE betType = 0 LIMIT 1")
    abstract fun observeSingleBet(): Flow<BetBean?>

    @Query("SELECT * FROM BetBean")
    abstract suspend fun getBetSheet(): List<BetBean>

    @Query("SELECT * FROM BetBean WHERE gameId = :id")
    abstract suspend fun getBetById(id: Int): BetBean?

    @Query("SELECT COUNT(*) FROM BetBean WHERE betType = 1")
    abstract fun observeComboBetCount(): Flow<Int>

    @Query("SELECT * FROM BetBean WHERE betType = 1")
    abstract fun observeComboBet(): Flow<List<BetBean>>

    @Query("UPDATE BetBean SET betType = :type WHERE gameId = :id")
    abstract suspend fun updateBetType(id: Int, type: BetTypeEnum)
    /**
     * 移除非roundId的投注記錄
     */
    @Query("delete from BetBean")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM BetBean WHERE gameId = :id")
    abstract suspend fun removeBet(id: Int)

}