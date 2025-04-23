package arch.cayenne.module.handicap.data

data class LetBallBean(
    val id: Long,
    val type: String,
    val tips: String,
    val list: List<LetBallItem>
)

data class LetBallItem(
    val id: Long,
    val type: String,
    val homeScore: Int,
    val awayScore: Int,
    val homeType: String,
    val awayType: String,
    val msg: String
)

