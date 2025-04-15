package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.LiveVideoBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LiveVideoDao : BaseDao<LiveVideoBean>() {

    @Query("SELECT * FROM LiveVideoBean LIMIT 1")
    abstract fun observeLiveVideoBean(): Flow<LiveVideoBean?>


    @Query("UPDATE LiveVideoBean SET url = :url ")
    abstract fun updateUrl(url: String)


}