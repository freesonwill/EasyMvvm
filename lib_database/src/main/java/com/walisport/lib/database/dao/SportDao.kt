package com.walisport.lib.database.dao

import androidx.room.Dao
import com.walisport.lib.database.entity.SportBean

@Dao
abstract class SportDao : BaseDao<SportBean>() {
}