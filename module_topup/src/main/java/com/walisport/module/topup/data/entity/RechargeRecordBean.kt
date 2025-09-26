package com.walisport.module.topup.data.entity

/**
 *
 * @date: 2025/8/8 17:14
 * @description:
 */
data class RechargeRecordBean(
    val iid: String, //充值记录id
    val type: Int,//提现类型
    val status: Int,//提现状态
    val time: Long,//提现时间
    val amount: String, //充值金额或数量
)