package com.walisport.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bet_bean")
data class BetBean(
    @PrimaryKey
    val gameId: Int,
    val betTeamName: String,
    val handicap: String,
    val odds: Float,
    val status: Int,
    val leagueName: String,
    val matchName: String,
    var isBetStop: Boolean = false,
)