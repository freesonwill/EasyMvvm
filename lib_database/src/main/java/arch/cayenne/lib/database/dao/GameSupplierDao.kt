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

    //设置单个id为选中为1,其他设置为0
    @Query("UPDATE GameSupplierDataModel SET isSelected = 0 WHERE gameTypeId = :gameTypeId")
    abstract suspend fun deselectAllInGroup(gameTypeId: Int)

    @Query("UPDATE GameSupplierDataModel SET isSelected = 1 WHERE gameTypeId = :gameTypeId AND id = :id")
    abstract suspend fun selectById(gameTypeId: Int, id: Int)

    suspend fun selectSupplierId(gameTypeId: Int, id: Int) {
        deselectAllInGroup(gameTypeId)
        selectById(gameTypeId, id)
    }


    @Query("SELECT * FROM GameSupplierDataModel WHERE gameTypeId = :gameTypeId")
    abstract fun observeSupplierGameTypeId(gameTypeId: Int): Flow<List<GameSupplierDataModel>>

    // 一次性把该类型下所有选中状态清0
    @Query("UPDATE GameSupplierDataModel SET isSelected = 0 WHERE gameTypeId = :type")
    abstract fun clearSelectedByType(type: Int)

    /**
     * 根據 ID 查詢供應商
     */
    @Query("SELECT * FROM GameSupplierDataModel WHERE id = :id LIMIT 1")
    abstract suspend fun querySupplierById(id: Int): GameSupplierDataModel?

}