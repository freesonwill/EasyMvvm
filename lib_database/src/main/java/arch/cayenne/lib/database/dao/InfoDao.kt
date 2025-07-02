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

    @Query("SELECT * FROM InfoBean limit 1")
    abstract fun observeBalance(): Flow<InfoBean>

    @Query("SELECT balance FROM InfoBean limit 1")
    abstract suspend fun getBalance(): Long

    @Query("SELECT currency FROM InfoBean limit 1")
    abstract suspend fun getCurrency(): String

    @Query("SELECT login FROM InfoBean limit 1")
    abstract fun isLogin(): Boolean

    @Query("UPDATE InfoBean SET login = :isLogin WHERE uid = :uid")
    abstract fun setLogin(uid: Int, isLogin: Boolean)

}