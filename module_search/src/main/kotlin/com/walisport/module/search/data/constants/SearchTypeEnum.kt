package com.walisport.module.search.data.constants

import java.io.Serializable

/**
 * 搜索类型枚举类
 * @property code 搜索类型的整数代码
 */
enum class SearchTypeEnum(val code: Int): Serializable {
    // 1-普通词  2-热门词  10-球员id  11-球队id  12-联赛id
    NORMAL_WORD(1),
    HOT_WORD(2),
    PLAYER_ID(10),
    TEAM_ID(11),
    TOURNAMENT_ID(12),
    UNKNOWN(-1);

    companion object {
        fun fromCode(code: Int): SearchTypeEnum {
            return entries.firstOrNull { it.code == code } ?: UNKNOWN
        }
    }
}