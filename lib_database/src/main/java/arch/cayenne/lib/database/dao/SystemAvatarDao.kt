package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.SystemAvatarBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SystemAvatarDao: BaseDao<SystemAvatarBean>() {


    @Query("SELECT * FROM SystemAvatarBean")
    abstract fun querySystemAvatar(): List<SystemAvatarBean>

}