package arch.cayenne.module.home.data.constants

/**
 * 比赛列表排序類型
 */
enum class MatchListSortType(val type: Int) {
    BY_HOT(1),     // 按热门联赛排序
    BY_TIME(0);   // 按比赛时间排序

    companion object {
        fun fromType(type: Int): MatchListSortType? {
            return values().find { it.type == type }
        }
    }

}


