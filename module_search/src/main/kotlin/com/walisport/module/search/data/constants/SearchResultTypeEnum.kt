package com.walisport.module.search.data.constants

/**
 * 搜索结果类型枚举类
 * @property value 结果类型的整数值
 */
enum class SearchResultTypeEnum(val value: Int) {
    // 0-无结果  1-结果列表  2-精准匹配球员  3-精准匹配球队 4-精准匹配联赛
    NONE(0),
    LIST(1),
    PLAYER(2),
    TEAM(3),
    TOURNAMENT(4);

    companion object {
        fun fromCode(value: Int): SearchResultTypeEnum {
            return entries.firstOrNull { it.value == value } ?: NONE
        }
    }
}