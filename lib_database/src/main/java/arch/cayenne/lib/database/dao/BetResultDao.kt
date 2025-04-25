package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetResultBean
import arch.cayenne.lib.database.entity.BetResultDetailBean
import arch.cayenne.lib.database.entity.BetResultStatusEnum
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetResultDao: BaseDao<BetResultBean>() {

    @Query("SELECT * FROM BetResultBean WHERE id = :id")
    abstract suspend fun getBetResultById(id: Long): BetResultBean?

    @Query("UPDATE BetResultDetailBean SET orderId = :orderId, status = :status WHERE betResultId = :resultId AND combo = :combo")
    abstract suspend fun updateDetail(resultId: Long, combo: Int, orderId: String, status: BetResultStatusEnum)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDetail(data: BetResultDetailBean): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDetail(data: List<BetResultDetailBean>): List<Long>

    @Query("SELECT * FROM BetResultDetailBean WHERE betResultId = :id")
    abstract fun observeResultDetailById(id: Long): Flow<List<BetResultDetailBean>>
}