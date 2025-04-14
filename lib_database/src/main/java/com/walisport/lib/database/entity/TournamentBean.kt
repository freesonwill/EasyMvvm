package com.walisport.lib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournament_bean")
data class TournamentBean(
    @PrimaryKey val id: Int,
    val name: String,
    val simpleName: String,
    val icon: String,
)
