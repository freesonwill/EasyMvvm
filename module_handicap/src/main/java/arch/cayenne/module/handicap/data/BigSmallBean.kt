package arch.cayenne.module.handicap.data

data class BigSmallBean(
    val id: Long,
    val type: String,
    val tips: String,
    val list: List<BigSmallItem>
)

data class BigSmallItem(
    val id: Long,
    val type: String,
    val homeScore: Int,
    val awayScore: Int,
    val homeTip: String,
    val awayTip: String,
    val homeType: Int,  //主队输赢结果类型 1全赢  2全输  3赢一半  4输一半  5退本金
    val awayType: Int,  //客队输赢结果类型 1全赢  2全输  3赢一半  4输一半  5退本金
    val msg: String
)