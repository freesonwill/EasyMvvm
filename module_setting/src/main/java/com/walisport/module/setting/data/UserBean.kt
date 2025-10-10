package com.walisport.module.setting.data

data class UserBean(
    val id: Int,
    val token: String,
    val nickname: String,
    val avatar: String,
    var isSelected: Boolean
)