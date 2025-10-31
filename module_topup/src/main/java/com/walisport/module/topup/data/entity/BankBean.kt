package com.walisport.module.topup.data.entity

data class BankBean(
    val id: Int,
    val isHeader: Boolean,
    val bankLetter: String,
    val bankName: String,
    val bankLogo: String
)
