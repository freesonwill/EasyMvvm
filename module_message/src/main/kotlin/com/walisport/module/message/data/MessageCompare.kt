package com.walisport.module.message.data

import androidx.recyclerview.widget.DiffUtil

class MessageCompare : DiffUtil.ItemCallback<NotificationBean>() {

    override fun areItemsTheSame(oldItem: NotificationBean, newItem: NotificationBean): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NotificationBean, newItem: NotificationBean): Boolean {
        return oldItem == newItem
    }
}