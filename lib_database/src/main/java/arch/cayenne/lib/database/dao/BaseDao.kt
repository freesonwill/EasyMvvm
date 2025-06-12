package arch.cayenne.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update

@Dao
abstract class BaseDao<T> {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(data: T): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun ignoreInsert(data: T): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(data: List<T>): List<Long>

    @Transaction
    @Update
    abstract fun update(data: T): Int

    @Transaction
    @Update
    abstract fun update(data: List<T>): Int
}