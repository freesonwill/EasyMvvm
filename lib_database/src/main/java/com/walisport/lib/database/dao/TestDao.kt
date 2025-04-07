package com.walisport.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.walisport.lib.database.entity.TestBean

// TODO 測試用，隨時可移除
@Dao
abstract class TestDao: BaseDao<TestBean>() {

    @Query("SELECT * FROM test_bean")
    abstract suspend fun getAll(): List<TestBean>
}