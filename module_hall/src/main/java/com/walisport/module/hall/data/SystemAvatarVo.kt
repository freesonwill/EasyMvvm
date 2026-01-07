package com.walisport.module.hall.data

data class SysAvatarVo(
    val id: Int, // 系统头像ID
    val groupId: Int, // 组ID[0-x]
    val subId: Int, // 组内ID[0-3] 顺时针 0:原图 1:转90 2:转180 3:转270
    val url: String // 图标地址
)
