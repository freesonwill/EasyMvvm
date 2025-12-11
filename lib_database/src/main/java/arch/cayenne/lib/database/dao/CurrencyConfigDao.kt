package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.CurrencyBean

@Dao
abstract class CurrencyConfigDao : BaseDao<CurrencyBean>() {

    @Query("SELECT * FROM CurrencyBean")
    abstract suspend fun getCurrencyConfigList(): List<CurrencyBean>

}