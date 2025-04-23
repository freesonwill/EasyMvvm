package arch.cayenne.module.handicap.data

data class SimulateBean(
    val id: Long,
    val type: String,
    val question: String,
    val score: String,
    val homeName: String,
    val awayName: String,
    val left: String,
    val right: String,
    val leftMsg: String,
    val rightMsg: String,
    val btnText: String
)
