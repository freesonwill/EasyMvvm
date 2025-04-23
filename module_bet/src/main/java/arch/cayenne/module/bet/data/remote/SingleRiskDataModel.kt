package arch.cayenne.module.bet.data.remote

data class SingleRiskDataModel(
    val matchId: Long,
    val selectionId: Long,
    val minAmount: Long,
    val maxAmount: Long,
)
