package com.walisport.lib.database.entity

import androidx.room.Entity

@Entity(tableName = "sport_category", primaryKeys = ["gameType","sportId"])
data class SportCategory(
    val gameType: Int,
    val sportId: Int,
    val matchCount: Int,
    val sportOrder: Int
)
