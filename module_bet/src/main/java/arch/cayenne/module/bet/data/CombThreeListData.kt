package arch.cayenne.module.bet.data

data class CombThreeListData(
    val iid: Long,
    val bet: Float = 0f,
    val win: Float = 0f,
    val selection: String,
    val odds: Float
)
