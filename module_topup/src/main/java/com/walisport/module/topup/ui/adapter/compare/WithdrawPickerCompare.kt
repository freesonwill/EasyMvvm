package com.walisport.module.topup.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.WithdrawFilterBean

class WithdrawPickerCompare: DiffUtil.ItemCallback<WithdrawFilterBean>() {
    override fun areItemsTheSame(oldItem: WithdrawFilterBean, newItem: WithdrawFilterBean): Boolean {
        return oldItem.txId == newItem.txId
    }

    override fun areContentsTheSame(oldItem: WithdrawFilterBean, newItem: WithdrawFilterBean): Boolean {
        return oldItem == newItem
    }
}