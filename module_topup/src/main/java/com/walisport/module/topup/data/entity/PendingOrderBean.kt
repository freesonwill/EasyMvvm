package com.walisport.module.topup.data.entity

data class PendingOrderBean(
    val orderId: String,
    val amount: Int,
    val paymentMethod: String,
    val isPaid: Boolean,
    val countdown: Int
)