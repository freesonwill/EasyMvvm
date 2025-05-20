package arch.cayenne.module.betslip.data.model

import arch.cayenne.module.betslip.data.constants.BetSlipDateFilterEnum

data class DateFilterBean(
    val title: String,
    val date: BetSlipDateFilterEnum,
    val isSelected: Boolean = false
)
