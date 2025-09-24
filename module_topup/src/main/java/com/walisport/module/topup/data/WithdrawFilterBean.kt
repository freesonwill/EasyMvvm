package com.walisport.module.topup.data

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.topup.R

data class WithdrawFilterBean(
    val txId: Int,
    val txName: String,
    var isSelected: Boolean = false
) {
    companion object {
        const val ALL_TYPE_ID = 0
        fun getAllTypeBean(): WithdrawFilterBean {
            return WithdrawFilterBean(
                txId = ALL_TYPE_ID,
                txName = R.string.tx_method.getString(),
                isSelected = true
            )
        }
    }
}