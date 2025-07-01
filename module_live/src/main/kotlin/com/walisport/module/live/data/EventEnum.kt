package com.walisport.module.live.data

import androidx.annotation.DrawableRes
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.walisport.module.live.R

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
enum class EventEnum(val type: Int, val desc: String, @DrawableRes val icon: Int?) {
    EVENT_GOAL(1, R.string.event_jq.getString(), R.drawable.icon_event_jq),
    EVENT_CORNER(2, R.string.event_jiao.getString(), R.drawable.icon_event_jiao),
    EVENT_YELLOW_CARD(3, R.string.event_yellow.getString(), R.drawable.icon_event_yellow),
    EVENT_RED_CARD(4, R.string.event_red.getString(), R.drawable.icon_event_red),
    EVENT_OFFSIDE(5, R.string.event_yue.getString(), R.drawable.icon_event_yue),
    EVENT_FREE(6, R.string.event_ryq.getString(), R.drawable.icon_event_ryq),
    EVENT_QIU(7, R.string.event_qmq.getString(), R.drawable.icon_event_qmq),
    EVENT_KICK(8, R.string.event_dq.getString(), R.drawable.icon_event_dq),
    EVENT_CHANGE(9, R.string.event_hr.getString(), R.drawable.icon_event_down),
    EVENT_TWO_RED(15, R.string.event_lhbh.getString(), R.drawable.icon_event_lhyh),
    EVENT_DIAN_FAD(16, R.string.event_dqwj.getString(), R.drawable.icon_event_dqwj),
    EVENT_WU(17, R.string.event_wlq.getString(), R.drawable.icon_event_wlq),
    EVENT_ASSISTS(18, R.string.event_zg.getString(), R.drawable.icon_event_zg),
    EVENT_SHANG_BU(19, R.string.event_stbs.getString(), R.drawable.icon_event_shang),
    EVENT_SHOOT_SUC(21, R.string.event_sz.getString(), R.drawable.icon_event_sz),
    EVENT_SHOOT_FAD(22, R.string.event_sp.getString(), R.drawable.icon_event_sp),
    EVENT_ATTACK(23, R.string.event_jg.getString(), R.drawable.icon_event_jg),
    EVENT_DANGER(24, R.string.event_wx.getString(), R.drawable.icon_event_wx),

    EVENT_BALL_CONTROL(25, R.string.event_kql.getString(), null),
    EVENT_PASS(40, R.string.event_cq.getString(), null),
    EVENT_PASS_SUC(41, R.string.event_cqcg.getString(), null),
    EVENT_PU(52, R.string.event_pj.getString(), null),
    EVENT_SHOOT(83, R.string.event_sm.getString(), null),

    EVENT_UP(200, R.string.event_up.getString(), R.drawable.icon_event_up),
    EVENT_DW(201, R.string.event_down.getString(), R.drawable.icon_event_down);

    companion object {
        fun getEventByCode(code: Int): EventEnum? {
            return entries.find { it.type == code }
        }
    }
}