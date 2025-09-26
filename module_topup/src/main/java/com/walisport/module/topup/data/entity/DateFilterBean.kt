package com.walisport.module.topup.data.entity

data class DateFilterBean(
    val title: String,
    val date: DateFilterEnum,
    val isSelected: Boolean = false
)