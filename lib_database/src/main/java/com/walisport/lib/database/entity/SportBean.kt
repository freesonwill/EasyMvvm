package com.walisport.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sport_bean")
data class SportBean(
    @PrimaryKey val sportId: Int,
    val sportName: String,
    var allMatchCount: Int = 0,
    var todayMatchCount: Int = 0,
    var earlyLinesMatchCount: Int = 0,
    var champion: Int = 0,
    var inPlayOdds: Int = 0,
)
