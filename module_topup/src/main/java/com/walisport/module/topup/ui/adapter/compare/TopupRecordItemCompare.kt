package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.RechargeRecordBean

class TopupRecordItemCompare : DiffUtil.ItemCallback<RechargeRecordBean>() {
    override fun areItemsTheSame(
        oldItem: RechargeRecordBean,
        newItem: RechargeRecordBean
    ): Boolean {
        return oldItem.transactionId == newItem.transactionId
    }

    override fun areContentsTheSame(
        oldItem: RechargeRecordBean,
        newItem: RechargeRecordBean
    ): Boolean {
        return oldItem == newItem

    }

}