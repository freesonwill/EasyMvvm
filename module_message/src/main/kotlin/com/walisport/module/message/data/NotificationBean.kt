package com.walisport.module.message.data

data class NotificationBean(
    val id: Long,
    val type: Int,
    val state: Int,
    val title: String,
    val content: String,
    val createTime: Long
)