package com.walisport.module.topup.data.entity

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R

data class RechargeFilterBean(
    val txId: Int,
    val txName: String,
    var isSelected: Boolean = false
) {
    companion object {
        const val ALL_TYPE_ID = 0
        fun getAllTypeBean(): RechargeFilterBean {
            return RechargeFilterBean(
                txId = ALL_TYPE_ID,
                txName = R.string.cz_method.getString(),
                isSelected = true
            )
        }
    }
}