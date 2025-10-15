package com.walisport.module.topup.ui.adapter.compare

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.PayMoneyBean

class PayMoneyCompare : DiffUtil.ItemCallback<PayMoneyBean>() {

    override fun areItemsTheSame(
        oldItem: PayMoneyBean,
        newItem: PayMoneyBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: PayMoneyBean,
        newItem: PayMoneyBean
    ): Boolean {
        return oldItem == newItem
    }
}