package com.walisport.module.topup.data.entity

data class FiatConfigBean(
    val id: Int,
    val name: String,
    val unit: String,
    val paymentMethods: List<PaymentMethod>
)

data class PaymentMethod(
    val type: String,
    val present: String,
    val quickAmounts: List<Int>,
    val minAmount: Int,
    val maxAmount: Int
)
