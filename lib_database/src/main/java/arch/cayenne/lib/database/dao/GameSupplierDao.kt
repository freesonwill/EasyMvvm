package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GameSupplierDao: BaseDao<GameSupplierDataModel>() {

    @Transaction
    @Query("SELECT * FROM GameSupplierDataModel WHERE gameTypeId = :gameTypeId")
    abstract suspend fun querySupplier( gameTypeId: Int): List<GameSupplierDataModel>

    @Query("SELECT * FROM GameSupplierDataModel WHERE gameTypeId = :gameTypeId")
    abstract suspend fun querySelectSupplier( gameTypeId: Int): List<GameSupplierDataModel>

    @Query("SELECT * FROM GameSupplierDataModel WHERE gameTypeId = :gameTypeId and id = :id")
    abstract suspend fun querySupplierId( gameTypeId: Int,id: Int): GameSupplierDataModel

    @Query("SELECT * FROM GameSupplierDataModel WHERE gameTypeId = :gameTypeId")
    abstract fun observeSupplierGameTypeId(gameTypeId: Int): Flow<List<GameSupplierDataModel>>

    // 一次性把该类型下所有选中状态清0
    @Query("UPDATE GameSupplierDataModel SET isSelected = 0 WHERE gameTypeId = :type")
    abstract fun clearSelectedByType(type: Int)


}