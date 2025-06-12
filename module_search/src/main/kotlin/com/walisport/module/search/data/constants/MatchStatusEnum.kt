package com.walisport.module.search.data.constants

import java.io.Serializable

/**
 * 比赛状态枚举类
 * @property code 状态码
 */
enum class MatchStatusEnum(val code: Int): Serializable {
    // 比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
    ENDED(0),
    POSTPONED(1),
    INTERRUPTED(2),
    CANCELED(3),
    NOT_STARTED(4),
    ONGOING(5),
    DELAYED(6),
    ABANDONED(7),
    PAUSED(8),
    UNKNOWN(-1);

    companion object {
        fun fromCode(code: Int): MatchStatusEnum {
            return entries.firstOrNull { it.code == code } ?: UNKNOWN
        }
    }
}