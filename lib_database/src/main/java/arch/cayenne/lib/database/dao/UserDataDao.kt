package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.UserDataBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDataDao : BaseDao<UserDataBean>() {

    @Query("SELECT balanceTotal FROM UserDataBean limit 1")
    abstract fun observeBalance(): Flow<Long?>

    @Query("SELECT * FROM UserDataBean limit 1")
    abstract suspend fun getUser(): UserDataBean?

    @Query("SELECT * FROM UserDataBean limit 1")
    abstract fun observeUser(): Flow<UserDataBean>
}