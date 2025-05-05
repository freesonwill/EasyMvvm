package com.walisport.module.live.data

/**
 *  1 进球
 *  2 角球
 *  3 黄牌
 *  4 红牌
 *  5 越位
 *  6 任意球
 *  7 球门球
 *  8 点球
 *  9 换人
 *  10 比赛开始
 *  11 中场
 *  12 结束
 *  13 半场比分
 *  15 两黄变红
 *  16 点球未进
 *  17 乌龙球
 *  18 助攻
 *  19 伤停补时
 *  21 射正
 *  22 射偏
 *  23 进攻
 *  24 危险进攻
 *  25 控球率
 *  26 加时赛结束
 *  27 点球大战结束
 *  28 VAR(视频助理裁判)
 *  29 点球(点球大战)
 *  30 点球未进(点球大战)
 *  83 射门数
 */
enum class EventEnum(val type: Int) {
    EVENT_GOAL(1),
    EVENT_CORNER(2),
    EVENT_YELLOW_CARD(3),
    EVENT_RED_CARD(4),
    EVENT_OFFSIDE(5),
    EVENT_FREE(6),
    EVENT_CHANGE(9),
    EVENT_SHOOT_SUC(21),
    EVENT_ATTACK(23),
    EVENT_DANGER(24),
    EVENT_BALL_CONTROL(25),
    EVENT_PASS(40),
    EVENT_PASS_SUC(41),
    EVENT_SHOOT(83)
}