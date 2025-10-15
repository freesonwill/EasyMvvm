package com.walisport.module.topup.data.entity

data class PayMethodBean(
    val id: Int,
    val payType: String,
    val isSelect: Boolean,
    val isRecommend:Boolean,
)