package com.walisport.lib.database.dao

import androidx.room.Dao
import com.walisport.lib.database.entity.TournamentBean

@Dao
abstract class TournamentDao: BaseDao<TournamentBean>() {
}