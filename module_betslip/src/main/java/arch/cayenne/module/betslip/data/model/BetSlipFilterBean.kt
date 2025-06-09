package arch.cayenne.module.betslip.data.model

data class BetSlipFilterBean(
    val sportIds: List<Int>,
    val matchId: Long,
    val startTime: Long?,
    val endTime: Long?,
)
