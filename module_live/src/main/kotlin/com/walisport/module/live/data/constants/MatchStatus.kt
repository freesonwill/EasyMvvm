package com.walisport.module.live.data.constants

/**
 * 比赛状态enum,
 * 比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
 *
 * @see galaxy.common.proto.Common.MatchBasicInfo.getStatus
 */
enum class MatchStatus(val code: Int) {
    FINISHED(0),    // 已结束
    POSTPONED(1),   // 推迟
    INTERRUPTED(2), // 中断
    CANCELED(3),    // 取消
    NOT_STARTED(4), // 未开赛
    IN_PROGRESS(5), // 进行中
    DELAYED(6),     // 延迟
    ABANDONED(7),   // 废弃
    PAUSED(8)       // 暂停
}