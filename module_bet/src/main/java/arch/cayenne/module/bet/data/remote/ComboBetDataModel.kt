package arch.cayenne.module.bet.data.remote

/**
 * 串关结果
 * @property isSuccessful
 * @property message
 * @property data
 */
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