package com.walisport.module.topup.data.entity

/**
 *
 * @date: 2025/8/8 17:14
 * @description:
 */
data class RechargeDetailBean(
    val transactionId: String, //充值记录id
    val amount: String, //充值金额或数量
    val payment: String, //充值方式
    val status: Int,//支付状态
    val timestamp: Long, //创建时间
)