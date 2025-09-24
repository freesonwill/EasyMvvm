package com.walisport.module.topup.ui.adapter

import android.annotation.SuppressLint
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import com.walisport.module.topup.data.entity.RechargeRecordBean
import com.walisport.module.topup.databinding.ItemRechargeRecordBinding

class RechargeRecordItemViewHolder(
    private val mBinding: ItemRechargeRecordBinding,
) : BaseViewHolder(mBinding) {

    @SuppressLint("SetTextI18n")
    fun init(data: RechargeRecordBean) {
        with(mBinding) {
            tvAmount.text = data.amount
        }
    }
}