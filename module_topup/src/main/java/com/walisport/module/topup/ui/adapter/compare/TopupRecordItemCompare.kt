package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import arch.cayenne.lib.database.entity.MatchWithMarkets
import arch.cayenne.lib.database.entity.RechargeRecordBean

class TopupRecordItemCompare : DiffUtil.ItemCallback<RechargeRecordBean>() {
    override fun areItemsTheSame(oldItem: RechargeRecordBean, newItem: RechargeRecordBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RechargeRecordBean, newItem: RechargeRecordBean): Boolean {
        return oldItem == newItem

    }

}