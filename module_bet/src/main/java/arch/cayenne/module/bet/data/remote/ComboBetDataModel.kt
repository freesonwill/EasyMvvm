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
    val orderStatus: Int, // 订单状态 0创建，1确认中，2拒单，3取消订单，4接单成功，5已结算
)