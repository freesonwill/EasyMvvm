package arch.cayenne.module.bet.data.remote

/**
 * 单关下注限额
 *
 * @property matchId
 * @property selectionId
 * @property minAmount
 * @property maxAmount
 */
data class SingleRiskDataModel(
    val matchId: Long,
    val selectionId: Long,
    val minAmount: Long,
    val maxAmount: Long,
)
