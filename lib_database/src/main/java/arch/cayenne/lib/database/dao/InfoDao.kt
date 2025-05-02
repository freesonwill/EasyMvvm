package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.InfoBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class InfoDao: BaseDao<InfoBean>() {

    @Query("SELECT * FROM InfoBean WHERE uid = :uid")
    abstract fun queryInfoUid(uid: Int): InfoBean?

    @Query("SELECT * FROM InfoBean limit 1")
    abstract fun queryInfo(): InfoBean?

    @Query("SELECT balance FROM InfoBean limit 1")
    abstract fun observeBalance(): Flow<Long>

    @Query("SELECT balance FROM InfoBean limit 1")
    abstract suspend fun getBalance(): Long

}