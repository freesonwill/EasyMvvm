package com.walisport.module.hall.data


sealed class GameAllRankingToday {
    data class GameAllRankingTodayData(
        val rank: Int,
        val playerName: String,
        val symbol: String,
        val betting: Double,
        val bonus: Double,
        val myself: Boolean,
    ) : GameAllRankingToday()

    data object GameAllRankingDashData : GameAllRankingToday()
}

