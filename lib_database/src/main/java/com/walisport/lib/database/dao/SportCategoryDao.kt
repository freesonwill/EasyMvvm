package com.walisport.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.walisport.lib.database.entity.SportCategory

@Dao
abstract class SportCategoryDao : BaseDao<SportCategory>() {
    @Query("SELECT * " +
            "FROM sport_category " +
            "WHERE gameType = :gameType order by sportOrder")
    abstract fun querySportsMatchCount(gameType: Int): List<SportCategory>

    @Query("SELECT sportId " +
            "FROM sport_category " +
            "WHERE gameType = :gameType order by sportOrder limit 1")
    abstract fun getDefaultSportId(gameType: Int) : Int
}