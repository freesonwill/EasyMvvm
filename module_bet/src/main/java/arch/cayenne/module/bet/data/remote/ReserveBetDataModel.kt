package arch.cayenne.module.bet.data.remote

/**
 * 預約下注结果
 *
 * @property isSuccessful
 * @property message
 */
data class ReserveBetDataModel(
    val isSuccessful: Boolean,
    val message: String,
)