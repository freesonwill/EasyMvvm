package com.walisport.module.topup.ui.adapter.compare

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.topup.data.entity.AddressBean

class AddressCompare : DiffUtil.ItemCallback<AddressBean>() {

    override fun areItemsTheSame(
        oldItem: AddressBean,
        newItem: AddressBean
    ): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: AddressBean,
        newItem: AddressBean
    ): Boolean {
        return oldItem == newItem
    }
}