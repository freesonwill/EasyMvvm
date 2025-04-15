package com.walisport.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.walisport.lib.database.entity.SportCategory

@Dao
abstract class SportCategoryDao : BaseDao<SportCategory>() {
    @Query("SELECT * " +
            "FROM sport_category " +
            "WHERE playType = :playType order by sportOrder"
    )
    abstract fun querySportsMatchCount(playType: Int): List<SportCategory>
}