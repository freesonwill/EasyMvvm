package arch.cayenne.module.home.data.constants

/**
 * 比赛列表排序類型
 */
enum class MatchListSortType(val type: Int) {
    BY_HOT(1) ,     // 按热门联赛排序
    BY_TIME(0)     // 按比赛时间排序
}
//从int 转换为MatchListSortType
fun Int.toMatchListSortType(): MatchListSortType {
    return when (this) {
        MatchListSortType.BY_HOT.type -> MatchListSortType.BY_HOT
        MatchListSortType.BY_TIME.type -> MatchListSortType.BY_TIME
        else -> MatchListSortType.BY_TIME
    }
}

