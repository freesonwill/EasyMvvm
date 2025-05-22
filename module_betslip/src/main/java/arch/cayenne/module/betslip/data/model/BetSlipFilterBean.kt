package arch.cayenne.module.betslip.data.model

data class BetSlipFilterBean(
    val sportId: Int,
    val matchId: Long,
    val startTime: Long?,
    val endTime: Long?,
)
