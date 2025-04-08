package com.walisport.module.live.data.model

data class GoalTrendBean @JvmOverloads constructor(
    val id: Int = 0,
    val timeStamp: Long,
    val isHome: Boolean,
    val type: Int,
    val minutes: Int,
    val rate: Int,
)
