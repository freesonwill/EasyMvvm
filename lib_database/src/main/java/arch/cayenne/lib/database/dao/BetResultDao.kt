package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.BetResultBean

@Dao
abstract class BetResultDao: BaseDao<BetResultBean>() {

    @Query("SELECT * FROM BetResultBean WHERE id = :id")
    abstract suspend fun getBetResultById(id: Long): BetResultBean?
}