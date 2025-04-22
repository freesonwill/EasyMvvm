package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import arch.cayenne.lib.database.entity.InfoBean

@Dao
abstract class InfoDao: BaseDao<InfoBean>() {

    @Query("SELECT * FROM InfoBean WHERE uid = :uid")
    abstract fun queryInfoUid(uid: Int): InfoBean?

}