package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.VideoSourceBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LiveVideoDao : BaseDao<LiveVideoBean>() {

    @Query("SELECT * FROM LiveVideoBean where matchId = :matchId")
    abstract fun observeLiveVideoBean(matchId: Long): Flow<LiveVideoBean?>

    @Query("UPDATE LiveVideoBean set source = :source where  matchId = :matchId")
    abstract fun updatePlayingId(source: List<VideoSourceBean>, matchId: Long)

    @Query("SELECT COUNT(*) FROM LiveVideoBean ")
    abstract suspend fun queryCount(): Int

    @Query("delete from LiveVideoBean")
    abstract suspend fun deleteAll()


}