package com.walisport.module.search.data.model

import java.io.Serializable

/** * 搜索结果的基础数据模型
 * @property id 唯一标识符
 * @property name 名称
 * @property icon 图标URL
 * @property color 颜色代码
 */
open class SearchResultBaseBean(
    open val id: Int,
    open val name: String,
    open val icon: String?,
    open val color: String?
): Serializable