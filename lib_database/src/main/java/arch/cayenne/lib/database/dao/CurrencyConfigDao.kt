package arch.cayenne.lib.database.dao

import androidx.room.Dao
import arch.cayenne.lib.database.entity.CurrencyBean

@Dao
abstract class CurrencyConfigDao : BaseDao<CurrencyBean>() {

}