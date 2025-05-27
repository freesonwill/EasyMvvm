package com.walisport.module.search.data.model
/**
 * @author: xiaoyang
 * @date: 2025/5/19 17:17
 * @description: 搜索记录
 */

/** * 搜索记录数据模型
 * @property uid 用户ID
 * @property record 搜索记录内容
 */
data class RecordBean(
    val uid: Int,
    var record: String = ""
)
