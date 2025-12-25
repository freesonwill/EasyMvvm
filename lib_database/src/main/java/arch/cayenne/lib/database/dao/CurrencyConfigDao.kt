package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import arch.cayenne.lib.database.entity.CurrencyBean

@Dao
abstract class CurrencyConfigDao : BaseDao<CurrencyBean>() {

    @Query("SELECT * FROM CurrencyBean")
    abstract suspend fun getCurrencyConfigList(): List<CurrencyBean>

    @Query(" SELECT *, INSTR(name, :firstChar) AS score " +
            "FROM CurrencyBean " +
            "WHERE name LIKE :pattern ORDER BY score ASC "
    )
    abstract suspend fun searchByKeyword(pattern: String, firstChar: String): List<CurrencyBean>

    //根据ccy获取币种信息
    @Query("SELECT * FROM CurrencyBean WHERE ccy = :ccy LIMIT 1")
    abstract suspend fun getCurrencyByCcy(ccy: String): CurrencyBean?

}