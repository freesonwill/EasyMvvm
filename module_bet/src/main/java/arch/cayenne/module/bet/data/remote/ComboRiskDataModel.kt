package arch.cayenne.module.bet.data.remote

data class ComboRiskDataModel(
    val serialValue: Int, // 多少串一關，0為全串關
    val minAmount: Long,
    val maxAmount: Long
)