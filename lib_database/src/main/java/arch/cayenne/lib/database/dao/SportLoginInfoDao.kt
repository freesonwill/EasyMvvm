package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.InfoBean
import arch.cayenne.lib.database.entity.SportLoginInfoBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SportLoginInfoDao: BaseDao<SportLoginInfoBean>() {

    @Query("SELECT isLogin FROM SportLoginInfoBean limit 1")
    abstract fun observerLogin(): Flow<Boolean?>

    //更新SportLoginInfoBean表中的isLogin字段
    @Query("UPDATE SportLoginInfoBean SET isLogin = :isLogin")
    abstract suspend fun updateLogin(isLogin: Boolean)

}