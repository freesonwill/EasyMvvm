package com.walisport.module.topup.data.entity

data class CurrencyBean(
    val id: Int,
    val coinName: String,
    val coinLogo: Int,
    var isSelect: Boolean
)
