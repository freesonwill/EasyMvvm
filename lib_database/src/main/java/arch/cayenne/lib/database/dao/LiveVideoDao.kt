package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.LiveVideoBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LiveVideoDao : BaseDao<LiveVideoBean>() {

    @Query("SELECT * FROM LiveVideoBean")
    abstract fun observeLiveVideoBean(): Flow<List<LiveVideoBean>?>

    @Query("UPDATE LiveVideoBean SET isPlaying = CASE WHEN id = :id THEN 1 ELSE 0 END ")
    abstract fun updatePlayingId(id: Int)

    @Query("SELECT COUNT(*) FROM LiveVideoBean ")
    abstract suspend fun queryCount(): Int

    @Query("delete from LiveVideoBean")
    abstract suspend fun deleteAll()


}