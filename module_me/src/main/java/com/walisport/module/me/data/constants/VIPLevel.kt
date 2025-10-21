package com.walisport.module.me.data.constants

/**
 *
 * @date: 2025/10/16 14:30
 * @description:
 *
 * VIP原有12个等级
 *
 * 铜 白银 黄金 铂金 钻石 绿钻 红钻 黑钻 星钻 陨钻 星辰 宇宙
 *
 * 后台设定xx-xx位白银，xx-xx位黄金
 */
enum class VIPLevel(val value: Int) {
    Copper(0),
    Silver(1),
    Gold(2),
    Platinum(3),
    Diamond(4),
    GreenDiamond(5),
    RedDiamond(6),
    BlackDiamond(7),
    StarDiamond(8),
    MeteoriteDiamond(9),
    Stars(10),
    Universe(11),
}