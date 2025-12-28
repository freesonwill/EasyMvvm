package arch.cayenne.lib.common.data.constants

/**
 * VIP等级
 * VIP原有12个等级
 * 铜 白银 黄金 铂金 钻石 绿钻 红钻 黑钻 星钻 陨钻 星辰 宇宙
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
        fun fromLevel(level: Int): VIPLevel {
            return when (level) {
                1 -> Copper
                2 -> Silver
                3 -> Gold
                4 -> Platinum
                5 -> Diamond
                6 -> GreenDiamond
                7 -> RedDiamond
                8 -> BlackDiamond
                9 -> StarDiamond
                10 -> MeteoriteDiamond
                11 -> Stars
                else -> Universe
            }
        }
    }
}

