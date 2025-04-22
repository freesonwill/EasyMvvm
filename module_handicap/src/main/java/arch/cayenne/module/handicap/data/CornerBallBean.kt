package arch.cayenne.module.handicap.data

data class CornerBallBean(
    val id: Long,
    val type: String,
    val homeScore: Int,
    val awayScore: Int,
    val homeTip: String,
    val awayTip: String,
    val homeType: String,
    val awayType: String,
    val msg: String
)