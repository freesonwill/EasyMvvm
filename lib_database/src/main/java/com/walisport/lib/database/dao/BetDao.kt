package com.walisport.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.walisport.lib.database.entity.BetBean
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BetDao: BaseDao<BetBean>() {

    @Query("SELECT * FROM BetBean WHERE status = 0 LIMIT 1")
    abstract fun observeSingleBet(): Flow<BetBean?>

    @Query("SELECT * FROM BetBean")
    abstract suspend fun getBetSheet(): List<BetBean>

    @Query("SELECT * FROM BetBean WHERE gameId = :id")
    abstract suspend fun getBetById(id: Int): BetBean?

    @Query("SELECT COUNT(*) FROM BetBean WHERE status = 1")
    abstract fun observeComboBetCount(): Flow<Int>

    @Query("SELECT * FROM BetBean WHERE status = 1")
    abstract fun observeComboBet(): Flow<List<BetBean>>

    /**
     * 移除非roundId的投注記錄
     */
    @Query("delete from BetBean")
    abstract suspend fun deleteAll()

    @Query("DELETE FROM BetBean WHERE gameId = :id")
    abstract suspend fun removeBet(id: Int)

}