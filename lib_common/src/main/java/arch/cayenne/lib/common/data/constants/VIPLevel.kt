package arch.cayenne.lib.common.data.constants

/**
 * VIP等级
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
    Universe(11);

    companion object {
        /**
         * 根據 VIP 等級數字轉換為對應的 VIPLevel enum
         */
        fun fromLevel(level: Int): VIPLevel {
            return when (level) {
                in 0..9 -> Copper
                in 10..19 -> Silver
                in 20..29 -> Gold
                in 30..39 -> Platinum
                in 40..49 -> Diamond
                in 50..59 -> GreenDiamond
                in 60..69 -> RedDiamond
                in 70..79 -> BlackDiamond
                in 80..89 -> StarDiamond
                in 90..99 -> MeteoriteDiamond
                in 100..109 -> Stars
                else -> Universe
            }
        }
    }
}

