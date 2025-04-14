package com.walisport.lib.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.walisport.lib.database.entity.TournamentCategory

@Dao
abstract class TournamentCategoryDao: BaseDao<TournamentCategory>() {

    @Query("SELECT * " +
            "FROM tournament_category " +
            "WHERE  sportId = :sportId and playType = :playType order by weight desc"
    )
    abstract fun queryTournamentBySportId(playType: Int, sportId: Int): List<TournamentCategory>
}