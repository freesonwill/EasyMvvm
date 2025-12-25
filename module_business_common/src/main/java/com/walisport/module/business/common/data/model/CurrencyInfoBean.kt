package com.walisport.module.business.common.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrencyInfoBean(
    val id: Int,                // 货币ID
    val isVirtual: Boolean,     // 是否虚拟货币
    val rate: Double,           // 汇率
    val unit: String?,          // 货币单位（法币如：¥$，虚拟货币是图片）
    val name: String            // 货币名称
) : Parcelable