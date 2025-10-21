package com.walisport.module.topup.data.entity

data class BetDetailBean(
    val id: Int,
    val payType: Int,
    val coinType: Int,
    val progress: Int,
    val betMoney: String,
    val payMoney: String,
    val betRate: String,
    val timestamp: Long
)