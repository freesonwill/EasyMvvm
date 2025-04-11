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
    var betType: BetTypeEnum, // 0: 單注 1: 串關 2: 預約
    val leagueName: String,
    val matchName: String,
    var isBetStop: Boolean = false,
)

enum class BetTypeEnum {
    SINGLE, COMBO, RESERVE
}

enum class BetStatusEnum {
    PENDING, // 待下注
    CLOSE, // 盤口關閉
    BETTING, // 下注中
    PLACED // 下注完成
}