package com.xcjh.app.bean
import androidx.annotation.Keep


@Keep
data class TT(
    val assist1Id: Int = 0,
    val assist1Name: String = "",
    val assist2Id: Int = 0,
    val assist2Name: String = "",
    val awayScore: Int = 0,
    val homeScore: Int = 0,
    val inPlayerId: Int = 0,
    val inPlayerName: String = "",
    val outPlayerId: Int = 0,
    val outPlayerName: String = "",
    val playerId: Int = 0,
    val playerName: String = "",
    val position: Int = 0,
    val reasonType: Int = 0,
    val second: Int = 0,
    val time: Int = 0,
    val type: Int = 0,
    val varReason: Int = 0,
    val varResult: Int = 0
)