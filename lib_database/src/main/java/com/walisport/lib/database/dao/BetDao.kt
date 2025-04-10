package com.walisport.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.walisport.lib.database.entity.BetBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetDao: BaseDao<BetBean>() {

    @Query("SELECT * FROM BetBean")
    abstract fun observeBetSheet(): Flow<List<BetBean>>

    @Query("SELECT * FROM BetBean")
    abstract suspend fun getBetSheet(): List<BetBean>

    @Query("SELECT COUNT(*) FROM BetBean")
    abstract fun observeBetCount(): Flow<Int>

    /**
     * 移除非roundId的投注記錄
     */
    @Query("delete from BetBean")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM BetBean WHERE status = :status LIMIT 1")
    abstract suspend fun getSingleBet(status: Int = 0): BetBean?
}