package arch.cayenne.module.bet.data.remote

/**
 * 单投结果
 *
 * @property isSuccessful
 * @property message
 * @property orderId
 * @property orderStatus
 */
data class SingleBetDataModel(
    val isSuccessful: Boolean,
    val message: String,
    val orderId: String,
    val orderStatus: Int,
)