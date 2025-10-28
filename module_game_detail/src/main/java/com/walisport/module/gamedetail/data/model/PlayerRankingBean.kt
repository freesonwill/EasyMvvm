package com.walisport.module.gamedetail.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerRankingBean(
    val id: Int,            // 注单id
    val name: String,       // 用户名
    val currency: Int,      // 币种
    val bet: Long,          // 投注金额
    val multiple: Long,     // 倍数
    val bonus: Long,        // 奖金
    val timestamp: Long     // 投注时间
): Parcelable
