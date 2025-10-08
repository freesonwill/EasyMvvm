package com.walisport.module.setting.ui.adapter.compare

import androidx.recyclerview.widget.DiffUtil
import com.walisport.module.setting.data.UserBean

class UserCompare : DiffUtil.ItemCallback<UserBean>() {

    override fun areItemsTheSame(oldItem: UserBean, newItem: UserBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserBean, newItem: UserBean): Boolean {
        return oldItem == newItem
    }
}