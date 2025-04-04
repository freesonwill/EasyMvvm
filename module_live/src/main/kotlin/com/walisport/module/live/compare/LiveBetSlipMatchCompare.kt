package com.walisport.module.live.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.live.data.model.LiveBetSlipBean

class LiveBetSlipMatchCompare : DiffUtil.ItemCallback<LiveBetSlipBean>() {
    override fun areItemsTheSame(oldItem: LiveBetSlipBean, newItem: LiveBetSlipBean): Boolean {
   return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: LiveBetSlipBean, newItem: LiveBetSlipBean): Boolean {
        return oldItem.code == newItem.code
    }
}