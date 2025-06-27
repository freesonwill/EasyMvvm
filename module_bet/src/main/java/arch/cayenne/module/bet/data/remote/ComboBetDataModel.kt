package arch.cayenne.module.bet.data.remote

data class ComboBetDataModel(
    val isSuccessful: Boolean,
    val message: String,
    val data: List<ComboMultiBetInfo>
)

data class ComboMultiBetInfo(
    val orderId: String,
    val serialValue: Int, // 多少串一關，0為全串關
    val orderStatus: Int,
)