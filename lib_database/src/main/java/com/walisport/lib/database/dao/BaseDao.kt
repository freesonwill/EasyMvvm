package com.walisport.lib.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(data: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun ignoreInsert(data: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(data: List<T>): List<Long>

    @Update
    abstract fun update(data: T): Int

    @Update
    abstract fun update(data: List<T>): Int
}