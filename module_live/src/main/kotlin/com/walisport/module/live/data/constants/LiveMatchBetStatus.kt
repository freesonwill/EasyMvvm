package com.walisport.module.live.data.constants

/**
    int32 status = 4;         //0-默认 1-新增 2-修改 3-删除 (仅在推送时使用)
    0-Default 1-Add 2-Modify 3-Delete
 */
enum class LiveMatchBetStatus(val code: Int) {
    DEFAULT(0),    // 默认
    ADD(1),   // 新增
    MODIFY(2), // 修改
    DELETE(3)    // 删除
}