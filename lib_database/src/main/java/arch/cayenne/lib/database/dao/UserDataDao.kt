package arch.cayenne.lib.database.dao

import androidx.room.Dao
import arch.cayenne.lib.database.entity.UserDataBean

@Dao
abstract class UserDataDao : BaseDao<UserDataBean>() {

}