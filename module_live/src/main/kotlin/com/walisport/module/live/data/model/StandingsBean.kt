package com.walisport.module.live.data.model

data class StandingsBean @JvmOverloads constructor(
    val id: Int,
    val teams: List<StandingsTeam>
)

data class StandingsTeam(
    val rank: Int,
    val name: String,
    val cc: Int,
    val score: Int
)