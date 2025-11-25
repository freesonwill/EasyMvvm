package arch.cayenne.module.bet.data.remote

/**
 * 串关限额
 *
 * @property serialValue
 * @property minAmount
 * @property maxAmount
 */
data class ComboRiskDataModel(
    val serialValue: Int, // 多少串一關，0為全串關
    val minAmount: Long,
    val maxAmount: Long
)