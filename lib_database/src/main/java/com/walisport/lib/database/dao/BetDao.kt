package com.walisport.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.walisport.lib.database.entity.BetBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetDao: BaseDao<BetBean>() {

    @Query("SELECT * FROM bet_bean")
    abstract fun observeBetSheet(): Flow<List<BetBean>>

    @Query("SELECT COUNT(*) FROM bet_bean")
    abstract fun observeBetCount(): Flow<Int>
}