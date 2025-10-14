package com.walisport.module.topup.data.entity

data class BankCardBean(
    val id: Int,
    val bankName: String,
    val bankLogo: String,
    val bankNum: String,
    val bgColor: String,
    var isSelected: Boolean
)
