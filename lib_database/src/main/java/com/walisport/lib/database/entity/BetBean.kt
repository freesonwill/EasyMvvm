package com.walisport.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BetBean")
data class BetBean(
    @PrimaryKey
    val gameId: Int,
    val betTeamName: String,
    var handicap: String,
    var odds: Float,
    var status: Int,
    val leagueName: String,
    val matchName: String,
    var isBetStop: Boolean = false,
)