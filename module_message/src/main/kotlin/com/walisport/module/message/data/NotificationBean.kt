package com.walisport.module.message.data

data class NotificationBean(
    val id: Long,
    val type: Int,
    val bar: String,
    val title: String,
    val time: String,
    val content: String,
    val url: String,
    val money: String,
    val channel: String,
    val state: String
)